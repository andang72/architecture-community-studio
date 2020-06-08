package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringTest {

	private static Logger log = LoggerFactory.getLogger(StringTest.class);
	@Test
	public void test() {
		
		
		String fields = "imageLink,";

		
		boolean includeImageLink = org.apache.commons.lang3.StringUtils.contains(fields, "imageLink");  
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		log.debug("fields link : {} , tags : {}", includeImageLink, includeTags);
		
		
	}

}
