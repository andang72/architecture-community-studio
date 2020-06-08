package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.community.services.CommunityAdminService;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.CommunityContextHelper;
import architecture.ee.component.editor.DataSourceEditor;
import architecture.ee.component.editor.DataSourceEditor.PooledDataSourceConfig;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.util.ApplicationConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { 
		"classpath:application-community-context.xml",	
		"classpath:context/community-user-context.xml",
		"classpath:context/community-setup-context.xml",
		"classpath:context/community-utils-context.xml",
		"classpath:context/community-core-context.xml"})
public class SetupTest {

	private static Logger log = LoggerFactory.getLogger(SetupTest.class);
	
	@Autowired
	private Repository repository;
	
	@Autowired
	private ConfigService configService;

	@Autowired
	private CommunityAdminService communityAdminService;
	
	@Autowired
	private UserManager userManager;
	
	public SetupTest() {
		
	}

	@Test 
	public void testContext() { 
		File file = repository.getFile("context-config"); 
	} 
	  
	
	 
	public void createNewDataSource() {
		try { 
			PooledDataSourceConfig bean = DataSourceEditor.newPooledDataSourceBean("externalUserProviderPool");
			bean.setComment("외부 사용자 연동 데이버베이스");
			bean.setDriverClassName("oracle.jdbc.OracleDriver");
			bean.setUrl("jdbc:oracle:thin:@//222.122.47.196:1521/PODODB");
			bean.setUsername("U_HELPDESK");
			bean.setPassword("podoq23$");
			
			Map<String, String> props = new HashMap<String, String>();
			props.put("initialSize", "1");
			props.put("maxActive", "8");
			props.put("maxIdle", "8");
			props.put("maxWait", "-1");
			props.put("minIdle", "0");
			props.put("testOnBorrow", "false");
			props.put("testOnReturn", "false");
			props.put("testWhileIdle", "false");
			props.put("validationQuery", "select 1 from dual");
			bean.setConnectionProperties(props); 

			log.debug("repository database  {}", repository.getSetupApplicationProperties().getChildrenNames("database.externalUserProviderPool"));
			log.debug("configService database  {}", configService.getLocalProperties("database.externalUserProviderPool.connectionProperties"));
			 
			if( communityAdminService.testConnection(bean) ){ 
				communityAdminService.addConnectionProvider(bean);
				CommunityContextHelper.getComponent(bean.getName(), DataSource.class);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	 
	public void setUserProvider () {
		
		if( configService.isSetDataSource() ) {
			configService.setApplicationProperty("services.jdbcUserProvider.dataSource", "externalUserProviderPool");
			configService.setApplicationProperty("services.jdbcUserProvider.enabled", "false");
			
			log.debug( " jdbcUserProvider enabled : {} "  
				,
				configService.getApplicationBooleanProperty("services.jdbcUserProvider.enabled")
				
			);
			configService.setApplicationProperty("services.jdbcUserProvider.enabled", "true");
		 
			try {
				User user = userManager.getUser("dhson");
				userManager.deleteUser(user);
			} catch (UserNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
