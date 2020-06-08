package tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactory;
import architecture.ee.jdbc.sqlquery.mapping.MappedStatement;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.Repository;
import architecture.ee.util.ApplicationConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { 
	"classpath:application-community-context.xml",	
	"classpath:context/community-core-context.xml"})

public class SqlTest {

	private static Logger log = LoggerFactory.getLogger(SqlTest.class);
	
	
	@Autowired( required = true) 
	@Qualifier("repository")
	private Repository repository;
	
	@Autowired
    private SqlQueryFactory sqlQueryFactory;
	
	public SqlTest() { 
		
	}
	
	@Test
	public void testMappedStatement(){		
		for(MappedStatement ms : sqlQueryFactory.getConfiguration().getMappedStatements()) {
			log.debug("Mapped Statement id={}, resource={}", ms.getId(),  ms.getResource());
			
		}
	}
	


}
