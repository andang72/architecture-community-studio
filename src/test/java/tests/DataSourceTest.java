package tests;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.community.services.database.schema.Column;
import architecture.community.services.database.schema.Database;
import architecture.community.services.database.schema.DatabaseFactory;
import architecture.community.services.database.schema.Table;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { 
		"classpath:setup-context.xml"})


public class DataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceTest.class);

	
	@Autowired
	SetupService setupService;
	
	public DataSourceTest() {
	}
	
	@Test
	public void testCreateDataSource(){		
		boolean opt = false;
		logger.debug("SETUP {}", setupService );
		Properties connectionProperties = getOracleConnectionProperties("jdbc:oracle:thin:@//222.122.47.196:1521/PODODB", "U_HELPDESK", "podoq23$" );
		//Properties connectionProperties = getMysqlConnectionProperties("jdbc:mysql://222.122.47.9:3306/jasengdb?serverTimezone=Asia/Seoul", "u_jaseng", "Podoq23$" );
		setupService.registerDataSourceBean("dataSource", connectionProperties);
		DataSource dataSource = setupService.getComponent("dataSource", DataSource.class);
		
		setupService.testConnection(dataSource);  
		
		String catalog = null ;
		String schema  = null ;
		
		
		try {
		
			DB database = ExtendedJdbcUtils.extractDB(dataSource.getConnection());
			if( database == DB.ORACLE ) {
				logger.debug("this is oracle..."); 
			} 

		} catch (SQLException e1) {
		
		} 
		
		if(opt)
		try {
 
			Database db = DatabaseFactory.newDatabase(dataSource.getConnection(), catalog, schema);
			for( String name : db.getTableNames() ) {
				Table table = db.getTable(name);
				logger.debug( "table : {} {}", table.getName(), table.getPrimaryKey()!=null? table.getPrimaryKey().getName() : "NO" ); 
				
				for( String column : table.getColumnNames() ) {
					Column col = table.getColumn(column);
					logger.debug( "column : {} {}", col.getName(), col.getType() );  
				}
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	
	}
	
	private Properties getOracleConnectionProperties(String url, String user, String password) {
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("DriverClassName", "oracle.jdbc.OracleDriver");
		connectionProperties.setProperty("url", url );
		connectionProperties.setProperty("username", user  );
		connectionProperties.setProperty("password", password );   
		return connectionProperties;
	}
	
	private Properties getMysqlConnectionProperties(String url, String user, String password) {
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("DriverClassName", "com.mysql.cj.jdbc.Driver");
		connectionProperties.setProperty("url", url );
		connectionProperties.setProperty("username", user  );
		connectionProperties.setProperty("password", password );   
		return connectionProperties;
	}
	
}
