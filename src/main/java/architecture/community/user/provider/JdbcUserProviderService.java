package architecture.community.user.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import architecture.community.query.Utils;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.services.CommunityAdminService;
import architecture.community.user.AlreadyExistsException;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.user.UserProvider;
import architecture.community.user.UserTemplate;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.ee.component.event.PropertyChangeEvent;
import architecture.ee.jdbc.sqlquery.mapping.BoundSql;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;


/**
 * External User Provider based JDBC.
 * @author donghyuck
 *
 */
public class JdbcUserProviderService implements UserProvider {

	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private Type type = Type.JDBC;
	
	@Inject
	@Qualifier("repository")
	private Repository repository;
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired (required=false)
	private CommunityAdminService adminService;
	
	private CustomQueryJdbcDao customQueryJdbcDao ;
	
	private String name ;
	
	private AtomicBoolean isActive = new AtomicBoolean();
	
	public JdbcUserProviderService() {
		this.name = "jdbcUserProvider";
		this.customQueryJdbcDao = null;
		this.adminService = null;
	} 
	
	public void initialize(){		
		log.debug( "Initialize User Provicer : {} ({}) " , name, isEnabled() );
		if( isEnabled() ) { 
			if( adminService != null) {
				try { 
					createCustomQueryJdbcDao(); 
					if( ! isActive.get() ) {
						log.debug("Add Jdbc External User Provider ..");
						adminService.addUserProvider(this);
						isActive.set(true);
					}else {
						log.debug("Do nothing is already added.");
					}
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			} 
		}
	}	
	
	private void createCustomQueryJdbcDao() { 
		String key = String.format( "services.%s.dataSource" , name ); 
		String dataSource = configService.getApplicationProperty(key, null); 
		if( StringUtils.isEmpty( dataSource )) {
			log.warn("Datasource name can not be null : {} ", dataSource );
		}
		log.debug("Creating dao with datasource ({}).", dataSource );
		adminService.isExistAndCreateIfNotExist(dataSource); 
		this.customQueryJdbcDao = adminService.createCustomQueryJdbcDao(dataSource);  
	}
	
	private boolean isSetCustomQueryJdbcDao() {
		if( customQueryJdbcDao != null )
			return true;
		else
			return false;
	}
	
	public boolean isEnabled() {  
		String key = String.format( "services.%s.enabled" , name ); 
		return configService.getApplicationBooleanProperty(key, false); 
	} 
	
	
	public User getUser(User user) { 
		
		if(!isSetCustomQueryJdbcDao())
			return null;
		
		RowMapper<User> mapper = customQueryJdbcDao.getMapperSource("EXTERNAL_USER.USER_ROWMAPPER").createRowMapper(User.class); 
		
		Map<String, Object> additionalParameter = new HashMap<String, Object>();
		additionalParameter.put("_user", user );
		BoundSql sql = customQueryJdbcDao.getBoundSqlWithAdditionalParameter("EXTERNAL_USER.SELECT_USER_BY", additionalParameter); 
		User newUser;
		try {
			newUser = customQueryJdbcDao.getExtendedJdbcTemplate().queryForObject( sql.getSql(), mapper );
			setExternalUser( newUser );
		} catch (DataAccessException e) {
			return null;
		}
		return newUser ;
	}
	
	private void setExternalUser(User user) {
		if( user instanceof UserTemplate ) {
			((UserTemplate)user).setExternal(true);
		}
	}
 
	public Iterable<User> getUsers() { 
		return null;
	}
 
	public Iterable<String> getUsernames() { 
		return null;
	}
 
	public boolean supportsUpdate() { 
		if( isEnabled() ) {
			String key = String.format( "services.%s.supportsUpdate" , name ); 
			return configService.getApplicationBooleanProperty(key, false); 
		}
		return false;
	}
 
	public void update(User user) throws UnsupportedOperationException { 
		
	}
 
	public User create(User user) throws AlreadyExistsException, UnsupportedOperationException { 
		return null;
	}
 
	public void delete(User user) { 
		
	}
 
	public boolean supportsPagination() { 
		if( isEnabled() ){
			String key = String.format( "services.%s.supportsPagination" , name ); 
			return configService.getApplicationBooleanProperty(key, false); 
		}
		return false;
	}
 
	public int getCount() throws UnsupportedOperationException { 
		return 0;
	}
	
	public ItemList findUsers(DataSourceRequest request, UserManager userManager) { 
		if( request.getPageSize() == 0)
			request.setPageSize(30);
		request.setStatement("EXTERNAL_USER.COUNT_USERS_BY_REQUEST");	
		int totalCount = Utils.queryForObject(customQueryJdbcDao, request, Integer.class);
		
		List<User> users = new ArrayList<User>(totalCount);
		if( totalCount > 0) {
			request.setStatement("EXTERNAL_USER.FIND_USERNAMES_BY_REQUEST");		
			List<String> usernames = Utils.list(customQueryJdbcDao, request, String.class);
			for( String username : usernames ) {
				try {
					users.add(userManager.getUser(username));
				} catch (UserNotFoundException e) {
				}
			}
		}
		return new ItemList(users, totalCount);
	}
	
	public Iterable<User> getUsers(int i, int j) { 
		return null;
	}
 
	public String getName() {
		return name;
	}
	
	@EventListener(condition = "#event.propertyName  matches 'services.jdbcUserProvider.[a-zA-Z\\s]+'")
	public void handlePropertiesRefreshedEvent(PropertyChangeEvent event) { 
		if("services.jdbcUserProvider.enabled".equals(event.getPropertyName()) && event.getNewValue() != null ) {
			log.debug( "ExternalJdbcUserProvider enabled : {}",  Boolean.parseBoolean(event.getNewValue().toString()) );
			if( Boolean.parseBoolean(event.getNewValue().toString()) ) {
				initialize();
			}else {
				// disabled..
			}
		}
	}
	
	public Type getType() {
		return type;
	}
	
}
