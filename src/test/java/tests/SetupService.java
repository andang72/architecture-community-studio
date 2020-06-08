package tests;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class SetupService implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(SetupService.class);

	private ApplicationContext applicationContext = null;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public SetupService() { 
	}
 
	public boolean testConnection(DataSource dataSource) { 
		try {
			JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() { 
				public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException { 
					logger.debug( "Database Connection Test  : {}" , true );
					logger.debug( "Database Product Name : {}" , dbmd.getDatabaseProductName() );
					logger.debug( "Database Product Version : {} " , dbmd.getDatabaseProductVersion() );
					logger.debug( "JDBC DriverName : {} " , dbmd.getDriverName() );
					logger.debug( "JDBC Driver Version : {} " , dbmd.getDriverVersion());
					return dbmd;
				} 
			}); 
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	public <T> T getComponent(String requiredName, Class<T> requiredClass) {	
		return applicationContext.getBean(requiredName, requiredClass);
	}
	
	public void registerDataSourceBean(String name, Properties connectionProperties) {

		AutowireCapableBeanFactory factory =  applicationContext.getAutowireCapableBeanFactory();
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory; 
		if(registry.containsBeanDefinition(name)) {
			registry.removeBeanDefinition(name);
		} 
		GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition();
		
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
		mutablePropertyValues.add("url", connectionProperties.getProperty("url"));
		mutablePropertyValues.add("username", connectionProperties.getProperty("username"));
		mutablePropertyValues.add("password", connectionProperties.getProperty("password"));
		mutablePropertyValues.add("connectionProperties", connectionProperties);
		
		myBeanDefinition.setBeanClass(DriverManagerDataSource.class);
		myBeanDefinition.setPropertyValues(mutablePropertyValues);
		registry.registerBeanDefinition(name, myBeanDefinition); 
	}

}
