package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.sf.ehcache.Cache;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = {
		"classpath:context/community-ehcache-context.xml", 
		"classpath:test-ehcahce-context.xml" })
public class CacheTest {

	@Autowired
	@Qualifier("userIdCache")
	private Cache userIdCache;
	
	private static Logger log = LoggerFactory.getLogger(CacheTest.class);
	
	@Test
	public void testCache () {
		log.debug("{}", userIdCache);
	}
}
