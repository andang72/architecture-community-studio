package tests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.community.web.spring.controller.data.secure.mgmt.ResourceType;

public class ResourceTypeTest {

	private static final Logger logger = LoggerFactory.getLogger(ResourceTypeTest.class);
	
	@Test
	public void test() {
		
		logger.debug( 
		ResourceType.HTML.name()
		);
		
		logger.debug( 
				ResourceType.valueOf("jsp".toUpperCase()).toString()
		);
		
	}

}
