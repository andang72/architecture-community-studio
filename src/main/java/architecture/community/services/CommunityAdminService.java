package architecture.community.services;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import architecture.community.admin.menu.MenuComponent;
import architecture.community.admin.menu.SetupMenuService;
import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.query.CommunityCustomQueryService;
import architecture.community.query.CustomQueryService;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.services.mail.MailServicesConfig;
import architecture.community.user.MultiUserManager;
import architecture.community.user.UserProvider;
import architecture.ee.component.editor.DataSourceEditor;
import architecture.ee.component.editor.DataSourceEditor.PooledDataSourceConfig;
import architecture.ee.component.event.PropertiesRefreshedEvent;
import architecture.ee.component.platform.DiskUsage;
import architecture.ee.component.platform.ManagementService;
import architecture.ee.component.platform.MemoryUsage;
import architecture.ee.component.platform.SystemInfo;
import architecture.ee.exception.ComponentNotFoundException;
import architecture.ee.service.Repository;
import architecture.ee.spring.jdbc.datasource.DataSourceFactoryBean;
import architecture.ee.util.ApplicationConstants;

public class CommunityAdminService implements ApplicationContextAware , ManagementService {
	
	private static final Logger logger = LoggerFactory.getLogger(CommunityAdminService.class);

	private ApplicationContext applicationContext = null;

	@Autowired(required = false)
	@Qualifier("repository")
	private Repository repository;
	
	@Autowired(required = false)
	@Qualifier("admin.menuService")
	private SetupMenuService menuService;
	
	@Autowired(required = false)
	@Qualifier("admin.managementService")
	private CommunityManagementService managementService; 
	
	@Autowired(required = false)
	private ApplicationEventPublisher applicationEventPublisher;
	

	public CommunityAdminService() {  
		
	} 
	
	private boolean isSetMenuService() {
		return menuService != null;
	}

	private boolean isSetManagementService() {
		return managementService != null;
	}
	
	public boolean isSetupComplete() {
		return repository.getSetupApplicationProperties().getBooleanProperty(ApplicationConstants.SETUP_COMPLETE_PROP_NAME, false);
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	private void autowire(Object bean) {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
	}
	 
	// System Information ..
	
	public SystemInfo getSystemInfo() {
		if( isSetManagementService() )
			return managementService.getSystemInfo();
		return null;
	}

	public MemoryUsage getMemoryUsage() {
		if( isSetManagementService() )
			return managementService.getMemoryUsage();
		return null;
	}

	public List<DiskUsage> getDiskUsages(){
		if( isSetManagementService() )
			return managementService.getDiskUsages();
		return null;
	} 
	
	// menu access !!
	public MenuComponent getMenu(String menuName) {
		if( isSetMenuService() )
			return menuService.getMenu(menuName);
		return null;
	}
	
	public void reloadMenu() {
		if( isSetMenuService() )
			menuService.reload();
	}
	
	// CREATING -- may be setup 
	
	public void addUserProvider ( UserProvider userProvider ) {  
		MultiUserManager userManagerToUse = applicationContext.getBean(MultiUserManager.class);
		userManagerToUse.addUserProvider(userProvider); 
	}

	
	/**
	 * 인자로 전달된 데이터소스로 사용자정의 쿼리 서비스를 생성한다.
	 * 
	 * @param dataSource datasource bean name.
	 * @return
	 */
	public CustomQueryJdbcDao createCustomQueryJdbcDao(String dataSource) {  
		try {
			DataSource dataSourceToUse = applicationContext.getBean(dataSource, DataSource.class);
			return createCustomQueryJdbcDao(dataSourceToUse);
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", dataSource, DataSource.class.getName() ), e);
		} 
	}  
	
	public CustomQueryJdbcDao createCustomQueryJdbcDao(DataSource dataSource) {  
		try { 
			CustomQueryJdbcDao customQueryJdbcDao = new CustomQueryJdbcDao();
			autowire(customQueryJdbcDao);
			customQueryJdbcDao.setDataSource(dataSource);
			return customQueryJdbcDao;
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", dataSource, DataSource.class.getName() ), e);
		} 
	}  
	
	 
	public CustomQueryService createCustomQueryService(String dataSource) {  
		try { 
			DataSource dataSourceToUse = applicationContext.getBean(dataSource, DataSource.class);
			return createCustomQueryService(dataSourceToUse);
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", dataSource, DataSource.class.getName() ), e);
		} 
	}	
	
	public CustomQueryService createCustomQueryService(DataSource dataSource) {  
		try {  
			CommunityCustomQueryService customQueryService = new CommunityCustomQueryService();
			autowire(customQueryService); 
			
			CustomQueryJdbcDao customQueryJdbcDao = new CustomQueryJdbcDao();
			autowire(customQueryJdbcDao); 
			customQueryJdbcDao.setDataSource(dataSource); 
			
			customQueryService.setCustomQueryJdbcDao(customQueryJdbcDao); 
			
			return customQueryService;
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", dataSource, DataSource.class.getName() ), e);
		} 
	}	
	
	/**
	 * dataSourceName 에 해당하는 데이터소스가 없는 경우 생성한다.
	 * 성성을 위해서는 반듯이 startup-config.xml 에 정의되어 있어야 한다.
	 * 
	 * @param beanName
	 * @return
	 */
	public boolean isExistAndCreateIfNotExist(String beanName ) { 
		logger.debug("Checking context contain bean ({}): {}", beanName , applicationContext.containsBeanDefinition(beanName));
		if( !applicationContext.containsBeanDefinition(beanName) ) {
			
			String key = String.format( "database.%s" , beanName ); 
			for( String name : repository.getSetupApplicationProperties().getChildrenNames(key)) {
				if( name.equals("pooledDataSourceProvider")) {
					PooledDataSourceConfig config = new PooledDataSourceConfig(beanName); 
					registerDataSourceBean(config);
				}
			} 
			return false;
		}
		return true; 
	} 
	
	
	public boolean isExists(String beanName,  Class<?> requiredType) {
		boolean exist = false;
		logger.debug( "beans for {}, {}", beanName, requiredType.getName() );
		/*
		for( String n : applicationContext.getBeanNamesForType(requiredType) )
		{
			if ( StringUtils.equals(beanName, n) ) {
				exist = true;
				break;
			}
		}
		*/
		
		try {
			Object obj = applicationContext.getBean(beanName, requiredType);
			exist = true;
			if(obj != null)
				logger.debug("exist {}", obj.getClass().getName());
		} catch (BeansException e) {
		}
		return exist;
	}
	
	
	// EDITING MAILSENDER SETTING.
	
	
	
	
	// EDITING DATASOURCE SETTING.. 

	public boolean testConnection(PooledDataSourceConfig config) { 
		try {
			DataSource dataSource = createDriverManagerDataSource(config);  
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
	
	public void addConnectionProvider (PooledDataSourceConfig config) {
		addConnectionProviderToXml(config);
		registerDataSourceBean(config);
	}
	
	protected DataSource createDriverManagerDataSource(PooledDataSourceConfig config) {
		logger.debug("creating DriverManagerDataSource({}).", config.getDriverClassName() );
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(config.getDriverClassName());
		dataSource.setUrl(config.getUrl());
		dataSource.setUsername(config.getUsername());
		dataSource.setPassword(config.getPassword());
		return dataSource;
	}  
	
	protected void addConnectionProviderToXml (PooledDataSourceConfig config) {  
		logger.debug( "Save ConnectionProvider ({})" , config.getName() );
		File file = repository.getFile(ApplicationConstants.DEFAULT_STARTUP_FILENAME); 
		DataSourceEditor editor = new DataSourceEditor(file);
		editor.removeDataSource("externalUserProviderDBPool"); 
		editor.setPooledDataSource(config);  
		editor.write(); 
		if(applicationEventPublisher!=null)
			applicationEventPublisher.publishEvent(new PropertiesRefreshedEvent(this, "startup"));
	}  
	
	public void addMailSender (MailServicesConfig config) {
		registerMailSenderBean( config , true );
	}
	
	protected void registerDataSourceBean(PooledDataSourceConfig dataSource) {
		
		DataSourceFactoryBean bean = new DataSourceFactoryBean();
		bean.setProfileName(dataSource.getName()); 
		AutowireCapableBeanFactory factory =  applicationContext.getAutowireCapableBeanFactory();
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory; 
		if(registry.containsBeanDefinition(dataSource.getName())) {
			registry.removeBeanDefinition(dataSource.getName());
		} 
		GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition();
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
		mutablePropertyValues.add("profileName", dataSource.getName());
		myBeanDefinition.setBeanClass(DataSourceFactoryBean.class);
		myBeanDefinition.setPropertyValues(mutablePropertyValues);
		registry.registerBeanDefinition(dataSource.getName(), myBeanDefinition); 
		
	}
	
	protected void registerMailSenderBean(MailServicesConfig config, boolean overwriteIfExist) { 
		AutowireCapableBeanFactory factory =  applicationContext.getAutowireCapableBeanFactory();
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory; 
		
		if(overwriteIfExist && StringUtils.isNotBlank(config.getBeanName()) && registry.containsBeanDefinition( config.getBeanName() )) {
			registry.removeBeanDefinition(config.getBeanName());
		} 
		
		GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition();
		myBeanDefinition.setBeanClass(org.springframework.mail.javamail.JavaMailSenderImpl.class);
		myBeanDefinition.setBeanClassName(org.springframework.mail.javamail.JavaMailSenderImpl.class.getName());
		MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
		if(StringUtils.isNotBlank( config.getUsername() )) {
			mutablePropertyValues.add("username", config.getUsername());
		}
		if(StringUtils.isNotBlank( config.getPassword() )) {
			mutablePropertyValues.add("password", config.getPassword());
		}
		if(StringUtils.isNotBlank( config.getHost() )) {
			mutablePropertyValues.add("host", config.getHost());
		}
		if(StringUtils.isNotBlank( config.getDefaultEncoding())) {
			mutablePropertyValues.add("defaultEncoding", config.getDefaultEncoding());
		}
		if(StringUtils.isNotBlank( config.getProtocol() )) {
			mutablePropertyValues.add("protocol", config.getProtocol());
		}
		if(config.getPort() > 0) {
			mutablePropertyValues.add("port", config.getPort());
		} 
		
		Properties properties = new Properties();
		properties.putAll(config.getProperties());
		mutablePropertyValues.addPropertyValue("javaMailProperties", properties);
		myBeanDefinition.setPropertyValues(mutablePropertyValues);
		
		String beanName = BeanDefinitionReaderUtils.registerWithGeneratedName(myBeanDefinition, registry);
		config.setBeanName(beanName);
		
	}
	
	public <T> T getComponent(String requiredName, Class<T> requiredType) {
		
		if (applicationContext == null) {
			throw new IllegalStateException(CommunityLogLocalizer.getMessage("012005"));
		}		
		
		if (requiredType == null) {
			throw new IllegalArgumentException(CommunityLogLocalizer.getMessage("012001"));
		}
		
		try {
			if( StringUtils.isNotBlank(requiredName) ){
				return applicationContext.getBean(requiredName, requiredType);
			}else {
				return applicationContext.getBean(requiredType);
			}
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", requiredName, requiredType.getName() ), e);
		}
	}
	
	
}
