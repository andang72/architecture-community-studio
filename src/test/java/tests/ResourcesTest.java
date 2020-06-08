package tests;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcesTest {

	private static final Logger log = LoggerFactory.getLogger(MenuTest.class);
	
	@Test
	public void test() {
		String type = "template";
		String path = "";
		log.debug("resources {} > {} ", type,  path);
		if (!isValid(type)) {
			throw new IllegalArgumentException();
		}
		
		//fail("Not yet implemented");
	}

	private boolean isValid(String type) { 
		
		if (StringUtils.equals(type, "template") || StringUtils.equals(type, "script") || StringUtils.equals(type, "sql")) {
			return true;
		}
		return false;
	}
}
