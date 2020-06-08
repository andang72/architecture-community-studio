package architecture.community.services.setup;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.util.CommunityConstants;
import architecture.ee.component.editor.DataSourceConfig;
import architecture.ee.component.editor.DataSourceConfigReader;
import architecture.ee.component.editor.DataSourceEditor.PooledDataSourceConfig;
import architecture.ee.exception.ComponentNotFoundException;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.spring.jdbc.datasource.DataSourceFactoryBean;
import architecture.ee.util.ApplicationConstants;
import architecture.ee.util.StringUtils;

public class CommunitySetupService implements ApplicationContextAware , InitializingBean {
	
	private Logger log = LoggerFactory.getLogger(CommunitySetupService.class);
	
	private ApplicationContext applicationContext = null;
	 
	
	@Autowired(required=true)
	@Qualifier("repository")
	private Repository repository;
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;	
	
	
	@Autowired(required = false)
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	@Autowired(required = false)
	private ApplicationEventPublisher applicationEventPublisher;
	
	public CommunitySetupService() { 
		log.debug("Create {} Service." , this.getClass().getName());
	}
	
	public boolean isSetDataSource() {
		boolean isSetDataSource = dataSource != null ? true : false ;
		if( isSetDataSource ) {
			if (dataSource instanceof architecture.ee.jdbc.datasource.FailSafeDummyDataSource ) {
				isSetDataSource = false;
			} 
		} 
		return isSetDataSource;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
 
	public void afterPropertiesSet() throws Exception {
		boolean setupDatasource = configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_SETUP_DATASOURCES_ENABLED_PROP_NAME, false);
		log.debug("Setup DataSource - {}" ,  setupDatasource  );
		if( setupDatasource )
		{	
			log.debug("Setup DataSource - {}" , "START" );
			setupDataSources();
			log.debug("Setup DataSource - {}" , "END" );
		}
		
	}
	

	public void setupDatabase() { 
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(); 
		if(isSetDataSource()) { 
			DB db = DB.UNKNOWN;
			try {
				db = ExtendedJdbcUtils.extractDB(dataSource.getConnection());
			} catch (SQLException e) {
				log.warn(e.getMessage(), e);
			}
			
			if(db == DB.ORACLE) { 
				populator.addScript( getClassPathResource("schema/create-table-oracle.sql") );
			}else if (db == DB.MYSQL) {
				populator.addScripts(
					getClassPathResource("schema/create-table-mysql.sql"),
					getClassPathResource("schema/insert-data-mysql.sql")
				);
			}
			DatabasePopulatorUtils.execute(populator, dataSource);
		} 
	}
	
	private Resource getClassPathResource(String path) {
		return new ClassPathResource(path);
	}
	 
	private DataSource getDataSource (String dataSource) {  
		try {
			if(StringUtils.isNullOrEmpty(dataSource))
				dataSource = "dataSource";
			DataSource dataSourceToUse = applicationContext.getBean(dataSource, DataSource.class);
			return dataSourceToUse;
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", dataSource, DataSource.class.getName() ), e);
		} 
	} 
	
	private void autowire(Object bean) {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
	}
	
	
	private ConfigurableListableBeanFactory getConfigurableListableBeanFactory() {
		ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		return beanFactory;
	}
	
	private BeanDefinitionRegistry getBeanDefinitionRegistry() {
		AutowireCapableBeanFactory factory =  applicationContext.getAutowireCapableBeanFactory();
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory; 
		return registry;
	} 
	
	public void setupDataSources() { 
		ApplicationProperties config = repository.getSetupApplicationProperties();
		Collection<String> names = config.getChildrenNames(ApplicationConstants.DATABASE_PROP_NAME); 
		DataSourceConfigReader reader = getDataSourceConfigReader();
		for( String name : names ) {
			DataSourceConfig dsc = reader.getDataSoruceConfig(name);
			String beanName = dsc.getBeanName();
			if(StringUtils.isEmpty(beanName) && StringUtils.equals(name, "default" ))
			{
				dsc.setBeanName("dataSource");
			}
			if( !isExist(dsc)) {
				createDataSourceIfNotExist(dsc);
			}
		}
	}
	
	private boolean isExist(DataSourceConfig config) {
		boolean exist = false;
		for( String n : applicationContext.getBeanNamesForType(DataSource.class) )
		{
			if ( StringUtils.equals(config.getBeanName(), n) ) {
				exist = true;
				break;
			}
		}return exist;
	}
	

	private DataSourceConfigReader getDataSourceConfigReader() {
		return new DataSourceConfigReader(repository.getSetupApplicationProperties());
	}
	
	public void createDataSourceIfNotExist(DataSourceConfig config ) { 
		log.debug("Checking context contain bean ({}): {}", config.getBeanName() , applicationContext.containsBeanDefinition(config.getBeanName()));
		if( !applicationContext.containsBeanDefinition(config.getBeanName()) ) { 
			log.debug("register bean {} with class {}", config.getBeanName(), config.getClass().getName());
			if(config instanceof PooledDataSourceConfig) {
				registerDataSourceBean((PooledDataSourceConfig)config);  
				
				applicationContext.getBean(config.getBeanName(), DataSource.class);
			}
		}
	} 
	
	public void createDataSourceIfNotExist(String dataSource ) { 
		log.debug("Checking context contain bean ({}): {}", dataSource , applicationContext.containsBeanDefinition(dataSource));
		if( !applicationContext.containsBeanDefinition(dataSource) ) { 
			String key = String.format( "database.%s" , dataSource ); 
			for( String name : repository.getSetupApplicationProperties().getChildrenNames(key)) {
				if( name.equals("pooledDataSourceProvider")) {
					PooledDataSourceConfig config = new PooledDataSourceConfig(name);
					config.setName(dataSource);
					registerDataSourceBean(config);
				}
			}  
		}
	}  
	
	protected void registerDataSourceBean(PooledDataSourceConfig dataSource) { 
		
		DataSourceFactoryBean bean = new DataSourceFactoryBean();
		bean.setProfileName(dataSource.getName()); 
		BeanDefinitionRegistry registry = getBeanDefinitionRegistry(); 
		if(registry.containsBeanDefinition(dataSource.getBeanName())) {
			registry.removeBeanDefinition(dataSource.getBeanName());
		}
		GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition();
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
		mutablePropertyValues.add("profileName", dataSource.getName());
		myBeanDefinition.setBeanClass(DataSourceFactoryBean.class);
		myBeanDefinition.setPropertyValues(mutablePropertyValues);
		registry.registerBeanDefinition(dataSource.getBeanName(), myBeanDefinition);  
	}	
	
	protected void registerDataSourceBean(String beanName, Properties connectionProperties) {  
		BeanDefinitionRegistry registry = getBeanDefinitionRegistry();  
		if(registry.containsBeanDefinition(beanName)) {
			registry.removeBeanDefinition(beanName);
		} 
		GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition(); 
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
		mutablePropertyValues.add("url", connectionProperties.getProperty("url"));
		mutablePropertyValues.add("username", connectionProperties.getProperty("username"));
		mutablePropertyValues.add("password", connectionProperties.getProperty("password"));
		mutablePropertyValues.add("connectionProperties", connectionProperties); 
		myBeanDefinition.setBeanClass(DriverManagerDataSource.class);
		myBeanDefinition.setPropertyValues(mutablePropertyValues);
		registry.registerBeanDefinition(beanName, myBeanDefinition); 
	}

}
