package tests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

public class MediaTest {
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	public MediaTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testType () {
		MediaType m = MediaType.valueOf("text/javascript;charset=UTF-8");
		log.debug("{}" , m);
	}

}
