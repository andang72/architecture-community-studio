package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.exception.NotFoundException;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.services.CommunityAdminService;
import architecture.community.services.database.schema.Column;
import architecture.community.services.database.schema.Database;
import architecture.community.services.database.schema.DatabaseFactory;
import architecture.community.services.database.schema.Table;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.util.ServletUtils;
import architecture.ee.component.editor.DataSourceConfig;
import architecture.ee.component.editor.DataSourceConfigReader;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.util.ApplicationConstants;

@Controller("community-mgmt-datasource-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class DataSourceDataController {

	@Autowired( required = true) 
	@Qualifier("repository")
	private Repository repository;
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("adminService")
	private CommunityAdminService adminService;
	
	@Inject
	@Qualifier("taskExecutor")
	private TaskExecutor taskExecutor;
	
	private Logger logger = LoggerFactory.getLogger(DataSourceDataController.class);
	
	private com.google.common.cache.LoadingCache<String, DataSourceConfig> dataSourceConfigCache = null;
	
	private com.google.common.cache.LoadingCache<String, DatabaseInfo> databaseCache = null; 
	
	public DataSourceDataController() { }
	
	
	private DataSource getDataSource(DataSourceConfig key) {
		DataSource dataSource = adminService.getComponent(key.getBeanName(), DataSource.class);
		return dataSource;
	} 
	
	private DataSourceConfigReader getDataSourceConfigReader() {
		return new DataSourceConfigReader(repository.getSetupApplicationProperties());
	}
	
	private List<DataSourceConfig> listDataSourceConfig(){ 
		List<DataSourceConfig> list = new ArrayList<DataSourceConfig>();
		ApplicationProperties config = repository.getSetupApplicationProperties();
		Collection<String> names = config.getChildrenNames(ApplicationConstants.DATABASE_PROP_NAME); 
		DataSourceConfigReader reader = getDataSourceConfigReader();
		for( String name : names ) {
			try {
				list.add( getDataSourceConfigFromCache( name ));
			} catch (NotFoundException ignore) { }	
		}
		return list;
	}
	
	private DataSourceConfig getDataSourceConfigFromCache(String key) throws NotFoundException {
		if( dataSourceConfigCache == null) {
			dataSourceConfigCache = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(5, TimeUnit.MINUTES).build(		
				new CacheLoader<String, DataSourceConfig>(){			
					@Override
					public DataSourceConfig load(String key) throws Exception {  
						DataSourceConfigReader reader = getDataSourceConfigReader();
						DataSourceConfig dsc = reader.getDataSoruceConfig(key);
						dsc.setActive(adminService.isExists(dsc.getBeanName(), DataSource.class)); 
						return dsc;
					}
				}
			);	
		}
		try {
			return dataSourceConfigCache.get(key);
		} catch (ExecutionException e) {
			throw new NotFoundException(e);
		}
	}
	
	private DatabaseInfo getDatabaseInfoFromCache(DataSourceConfig key) { 
		
		if( databaseCache == null ) { 
			logger.debug("create cahce." );
			databaseCache = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(5, TimeUnit.MINUTES).build(		
				new CacheLoader<String, DatabaseInfo>(){			
					@Override
					public DatabaseInfo load(String key) throws Exception { 
						logger.debug("# 1. creat new with {}", key );  
						DataSourceConfig cfg = getDataSourceConfigFromCache( key );
						DataSource dataSource = getDataSource( cfg );  
						DatabaseInfo info = (DatabaseInfo) ExtendedJdbcUtils.extractDatabaseMetaData( dataSource, new DatabaseMetaDataCallback() { 
							public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {   
								return new DatabaseInfo(dbmd);
							} 
						}); 
						return info;
					}
				}
			);	
		} 
		logger.debug("# 0. finding database schema with {}", key.getName() );  
		DatabaseInfo databaseInfo = databaseCache.getIfPresent(key);
		if( databaseInfo == null) {
			try {
				databaseInfo = databaseCache.get(key.getName());
			} catch (Exception e) {
				logger.error("error create cache ... {}", e );
			}
		}
		logger.debug( "database from cache {}", databaseInfo );
		if( databaseInfo.done.get() == 0)
		{
			try {
				final DatabaseInfo toUse = databaseInfo ; 
				toUse.done.set(1); 
				logger.debug("# 2. database with {}", key.getName() );  
				taskExecutor.execute(new Runnable() {  
					public void run() {
						DataSource dataSource = getDataSource( key );
						logger.debug("2.0 using {}", dataSource);
							try {
								logger.debug("# 2.1 extract database '{}' .....", key.getName()); 
								toUse.database = DatabaseFactory.newDatabase(dataSource.getConnection(), toUse.catalogy, toUse.schema);
								toUse.done.set(2);
								logger.debug("# 2.2 extract database '{}' done.", key.getName()); 
							} catch (SQLException e) {
								toUse.done.set(0);
								logger.error(e.getMessage(), e);
							}
				}});
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		} 
		return databaseInfo;
	}

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/datasoruce/config/names.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getDataSourceNames( NativeWebRequest request){
		ApplicationProperties config = repository.getSetupApplicationProperties();
		Collection<String> names = config.getChildrenNames(ApplicationConstants.DATABASE_PROP_NAME); 
		List<String> list = new ArrayList<String>();
		for( String name : names ) {
			list.add(name);
		} 
		return new ItemList(list, list.size());
	} 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/datasource/config/{name}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public DataSourceConfig getDataSourcConfig(@PathVariable String name, NativeWebRequest request){
		return getDataSourceConfigReader().getDataSoruceConfig(name);
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/datasource/config/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<DataSourceConfig> getDataSourcConfig(NativeWebRequest request){ 
		List<DataSourceConfig> list = listDataSourceConfig();
		return list;
	} 
		
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/datasource/{name}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public DatabaseInfo getDatabaseInfo(@PathVariable String name, NativeWebRequest request) throws NotFoundException{  
		
		DataSourceConfig dsc = getDataSourceConfigFromCache(name); 
		if( adminService.isExists(dsc.getBeanName(), DataSource.class) ) {
			DatabaseInfo db = getDatabaseInfoFromCache(dsc);
			return db;
		}else {
			throw new NotFoundException();
		}
		
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/datasource/{name}/schema/table/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getDatabaseTables(@PathVariable String name, NativeWebRequest request) throws NotFoundException{  
		List<TableInfo> list = new ArrayList<TableInfo>();
		DataSourceConfig dsc = getDataSourceConfigFromCache(name); 
		if( adminService.isExists(dsc.getBeanName(), DataSource.class) ) {
			DatabaseInfo db = getDatabaseInfoFromCache(dsc);
			if( db != null && db.database != null)
			{
				for(String table : db.database.getTableNames()) {
					list.add(new TableInfo( db.database.getTable(table) ) );
				}
			}
		}else {
			logger.debug("datasource {} not active yet.", name );
		}
		ItemList itemList = new ItemList(list, list.size());
		return itemList;
	}
	
	
	private static final int [] EXCLUDE_DATA_TYPED = { java.sql.Types.BLOB , java.sql.Types.BINARY }; 
	private boolean isAllowed ( Column column ) {
		for( int t : EXCLUDE_DATA_TYPED ) {
			if( t == column.getType()) {
				return false;
			}
		}
		return true;
	} 
	
	private String queryForCount( Table table ) {
		StringBuilder sb = new StringBuilder("SELECT count(*) FROM ").append(table.getName()) ;
		return sb.toString();
	}
	
	private String queryForSelect( Table table ) {
		StringBuilder sb = new StringBuilder("SELECT ");
		for( String columnName : table.getColumnNames()) {
			Column column = table.getColumn(columnName);
			if( isAllowed (column) ) {
				sb.append(" ").append(column.getName()).append(",");
			}
		}
		int lastIndex = sb.lastIndexOf(",");
		if( lastIndex > 0 ) {
			sb.replace(lastIndex , lastIndex + 1, " " );
		}
		sb.append(" FROM ").append( table.getName() ); 
		return sb.toString();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/datasource/{dataSourceName}/schema/table/{tableName}/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getDatabaseTables(
			@PathVariable String dataSourceName, 
			@PathVariable String tableName, 
			@RequestBody DataSourceRequest dataSourceRequest,
			NativeWebRequest request) throws NotFoundException{  
		
		DataSourceConfig cfg = getDataSourceConfigFromCache(dataSourceName);  
		DatabaseInfo db = getDatabaseInfoFromCache(cfg);
		Table table = db.database.getTable(tableName); 
		
		DataSource dataSource = getDataSource( cfg );  
		CustomQueryJdbcDao customQueryJdbcDao = adminService.createCustomQueryJdbcDao(dataSource); 
		int totalCount = customQueryJdbcDao.getExtendedJdbcTemplate().queryForObject(queryForCount(table), Integer.class); 
		List<Map<String, Object>> list = Collections.EMPTY_LIST;
		if( totalCount > 0 ) { 
			if( dataSourceRequest.getPageSize() > 0 ){	
				list = customQueryJdbcDao.getExtendedJdbcTemplate().query( queryForSelect(table), dataSourceRequest.getSkip(),  dataSourceRequest.getPageSize(), new ColumnMapRowMapper() );					
			}else {
				list = customQueryJdbcDao.getExtendedJdbcTemplate().query( queryForSelect(table), new ColumnMapRowMapper());
			}
			
			for( Map<String, Object> row : list ) {
				Set<String> keys = row.keySet();
		        for (String k : keys) {
		           	Object v = row.get(k);
		           //	if( v != null )
		           //		logger.debug( "" + v.getClass().getName() );
		           	if ( v != null && v instanceof java.util.Date) {
		           		row.put( k, ServletUtils.getDataAsISO8601( (java.util.Date) v));
		           	}
		        } 
			}
			
		}
		ItemList itemList = new ItemList(list, totalCount ); 
		return itemList;
	}
	
	public static class DatabaseInfo implements Serializable { 
		// 0, 1, 2
		private AtomicInteger done = new AtomicInteger(0); 
		
		private DB type ;
		private String databaseProductName ;
		private String databaseProductVersion ;
		private String driverName ;
		private String driverVersion ;
		private String schema;
		private String catalogy; 
		private Database database;
		
		public DatabaseInfo(DatabaseMetaData dbmd) {
			this.type = DB.UNKNOWN;
			this.database = null;
			init( dbmd );
		}
		
		private void init (DatabaseMetaData dbmd) {
			this.schema = null;
			this.catalogy = null;
			this.type = ExtendedJdbcUtils.extractDB(dbmd); 
			try {
				this.databaseProductName = dbmd.getDatabaseProductName();
				this.databaseProductVersion = dbmd.getDatabaseProductVersion();
				this.driverName = dbmd.getDriverName();
				this.driverVersion = dbmd.getDriverVersion(); 
				if( this.type == DB.ORACLE || this.type == DB.MYSQL ) { 
					String username = dbmd.getUserName();
					if( StringUtils.isNotBlank(username)) {
						int endIndex = username.indexOf('@');
						if( endIndex > 0 ) {
							username = username.substring(0, endIndex);
						}
					}
					this.schema = username;
				}
			} catch (SQLException ignore) {  
				ignore.printStackTrace();
			}
		}
		
		public String getDatabaseProductName() {
			return databaseProductName;
		}
		public String getDatabaseProductVersion() {
			return databaseProductVersion;
		}
		public String getDriverName() {
			return driverName;
		}
		public String getDriverVersion() {
			return driverVersion;
		}
		public String getSchema() {
			return schema;
		}
		public String getCatalogy() {
			return catalogy;
		}
 
		public boolean isScrollResultsSupported() {
			return this.type.isScrollResultsSupported();
		}
		public boolean isFetchSizeSupported() {
			return type.isFetchSizeSupported();
		} 
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DatabaseInfo [");
			if (done != null)
				builder.append("done=").append(done).append(", ");
			if (type != null)
				builder.append("type=").append(type).append(", ");
			if (databaseProductName != null)
				builder.append("databaseProductName=").append(databaseProductName).append(", ");
			if (databaseProductVersion != null)
				builder.append("databaseProductVersion=").append(databaseProductVersion).append(", ");
			if (driverName != null)
				builder.append("driverName=").append(driverName).append(", ");
			if (driverVersion != null)
				builder.append("driverVersion=").append(driverVersion).append(", ");
			if (schema != null)
				builder.append("schema=").append(schema).append(", ");
			if (catalogy != null)
				builder.append("catalogy=").append(catalogy).append(", ");
			if (database != null)
				builder.append("database=").append(database);
			builder.append("]");
			return builder.toString();
		} 
	}
	
	public static class TableInfo implements Serializable { 
		
		private String name ;
		private String catalog ;
		private String schema;
		private Column primaryKey ;
		private List<Column> columns ;
		
		public TableInfo(Table table) {
			this.name = table.getName();
			this.catalog = table.getCatalog();
			this.schema = table.getSchema();
			this.primaryKey = table.getPrimaryKey();
			this.columns = new ArrayList<Column>();
			for( String c : table.getColumnNames()) {
				this.columns.add(table.getColumn(c));
			}
		}
		
		public String getName() {
			return name;
		}
		public String getCatalog() {
			return catalog;
		}
		public String getSchema() {
			return schema;
		}
		public Column getPrimaryKey() {
			return primaryKey;
		}
		public List<Column> getColumns() {
			return columns;
		}		
	}
	
}