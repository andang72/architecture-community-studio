package tests;

import org.apache.commons.lang3.StringUtils;
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
		
		log.debug( String.format("https://mangahentai.me/manga-hentai/lucky-guy/chapter-%s/", 1) );
	}
	
	
	@Test
	public void testString() {
		
		String x = "abcd";
		String y = "abcd";
		log.debug( "x==y : {}", x == y);  
		
		String z = new String("abcd");
		String zz = new String("abcd");
		
		log.debug( "y==z : {}", y == z);
		log.debug( "z==zz : {}", z == zz);
	}
		
	
	@Test 
	public void testReplaceFirst() {
		String str = "http://"; 
		str = StringUtils.replaceOnce(str, "http:", "https:");
		log.debug(str);
	}
	
	@Test 
	public void testFormat() {
		

		
		String str = String.format("https://%s/manga/%s/chapter-10", "hentai20.com", "fdsafdas");
		log.debug(str);
	}
}
