package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.community.services.CommunityAdminService;
import architecture.community.user.UserManager;
import architecture.ee.component.editor.DataSourceConfig;
import architecture.ee.component.editor.DataSourceConfigReader;
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
public class DataSourceConfigTest {

	private static Logger log = LoggerFactory.getLogger(DataSourceConfigTest.class);
	
	@Autowired
	private Repository repository;
	
	@Autowired
	private ConfigService configService;

	@Autowired
	private CommunityAdminService communityAdminService;
	
	@Autowired
	private UserManager userManager;
	
	public DataSourceConfigTest() {
		
	}

	@Test 
	public void testContext() { 
		File file = repository.getFile("context-config"); 
	} 
	
	@Test
	public void testGetDataSourceConfig() {
		
		ApplicationProperties config = repository.getSetupApplicationProperties();
		Collection<String> names = config.getChildrenNames(ApplicationConstants.DATABASE_PROP_NAME); 
		List<String> list = new ArrayList<String>();
		for( String name : names ) {
			list.add(name);
		} 
		
		log.debug("names {}", list);
		
		DataSourceConfigReader reader =  getDataSourceConfigReader();
		
		for( String name : list ) {
			log.debug("providers {} " ,reader.getProviderNames(name) );
			
			DataSourceConfig dataSourceConfig = reader.getDataSoruceConfig(name);
			if( dataSourceConfig != null)
				log.debug("DataSourceConfig : {} ", dataSourceConfig.getName() );
		}
	}
	
	private DataSourceConfigReader getDataSourceConfigReader() {
		return new DataSourceConfigReader(repository.getSetupApplicationProperties());
	}
	
	
}
