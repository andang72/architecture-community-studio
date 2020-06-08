package tests;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.community.util.CommunityConstants;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { 
		"classpath:application-community-context.xml",
		"classpath:context/community-user-context.xml",
		"classpath:context/community-ehcache-context.xml",
		"classpath:context/community-utils-context.xml",
		"classpath:context/community-core-context.xml"})

public class StartupTest {
	
	private static Logger log = LoggerFactory.getLogger(StartupTest.class);
 
	
	@Autowired
	private Repository repository;
	
	@Autowired
	private ConfigService configService;
	
	public StartupTest() { 
	}
	
	@Test
	public void testRepository() {
		log.info("repository");
	}
	
	@Test
	public void testConfigService() {
		log.info("ConfigService");
		
		configService.getApplicationPropertyNames();
		Locale locale = Locale.getDefault();
		
		log.debug("{} {} {}", 
		locale.getDisplayCountry(),
		locale.getDisplayLanguage(),
		locale.getDisplayVariant()
		);
		
		log.debug("{} {} {}" , locale.getCountry(), locale.getLanguage(), locale.getVariant() );
		
		//LOCALE 한국어 (대한민국), TIMEZONE 한국 표준시, ENCODING UTF-8
		  
		log.debug(
		"LOCALE_LANGUAGE_PROP_NAME = {}", configService.getApplicationProperty(CommunityConstants.LOCALE_LANGUAGE_PROP_NAME)
		);
		
		if( configService.isSetDataSource() && configService.isDatabaseInitialized()) {
			configService.setApplicationProperty(CommunityConstants.LOCALE_LANGUAGE_PROP_NAME, locale.getLanguage());
			configService.deleteApplicationProperty(CommunityConstants.LOCALE_LANGUAGE_PROP_NAME);
		}
	}
}
