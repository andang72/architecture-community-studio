package architecture.community.services.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.util.StringUtils;

import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.services.CommunityAdminService;
import architecture.ee.service.Repository;
import architecture.ee.spring.jdbc.InputStreamRowMapper;

public class CommunityExportService {

	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	
	@Autowired (required=false)
	private CommunityAdminService adminService;

	@Inject
	@Qualifier("repository")
	private Repository repository;
	
	protected synchronized File getExportsDir(String prefix) {
		File exportDir = repository.getFile("exports");
		if( !StringUtils.isEmpty( prefix ) )
		{
			exportDir = new File(exportDir, prefix);
		}	
		if( !exportDir.exists() )
		{
			exportDir.mkdir();	
		}	
		return exportDir;
	}
	
	private String getFilename( ImageDef def ) {
		StringBuilder builder = new StringBuilder();
		builder.append( def.getId() ).append("_").append(def.getFilename());
		return builder.toString();
	}
	
	public void saveAsFile( ImageDef def , InputStream input , String prefix ) throws IOException {
		File file = new File(getExportsDir(prefix), getFilename(def));
		FileUtils.copyInputStreamToFile(input, file);
	}
	
	public CustomQueryJdbcDao getCustomQueryJdbcDao(String dataSourceName ) {
		if( StringUtils.isEmpty( dataSourceName )) {
			log.warn("Datasource name can not be null : {} ", dataSourceName );
		}
		adminService.isExistAndCreateIfNotExist(dataSourceName); 
		return adminService.createCustomQueryJdbcDao(dataSourceName); 
	}
	
	public List<ImageDef> list (CustomQueryJdbcDao dao, String sql ){
		return dao.getExtendedJdbcTemplate().query(sql, new RowMapper<ImageDef>() { 
			public ImageDef mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new ImageDef( rs.getString(1), rs.getString(2));
		}});
	}
	
	public InputStream getInputStream(CustomQueryJdbcDao dao, String sql) {
		return dao.getExtendedJdbcTemplate().queryForObject( sql, new InputStreamRowMapper());
	}

	public InputStream getInputStream(CustomQueryJdbcDao dao, String sql, ImageDef def) {
		return dao.getExtendedJdbcTemplate().queryForObject( sql, new InputStreamRowMapper(), new SqlParameterValue (Types.VARCHAR,  def.getId() ) );
	} 
	
	public InputStream getInputStream(CustomQueryJdbcDao dao, String sql, String key) {
		return dao.getExtendedJdbcTemplate().queryForObject( sql, new InputStreamRowMapper(), new SqlParameterValue (Types.VARCHAR,  key ) );
	}  
	
}
