package architecture.community.services;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.scripting.support.RefreshableScriptTargetSource;
import org.springframework.scripting.support.ResourceScriptSource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.ee.exception.RuntimeError;
import architecture.ee.service.Repository;

public class CommunityGroovyService implements InitializingBean, ResourceLoaderAware , ApplicationContextAware {
	
	public static final String INLINE_SCRIPT_PREFIX = "inline:";
	
	public static final String JDBC_SCRIPT_PREFIX = "jdbc:";
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	 
	protected ReentrantLock lock = new ReentrantLock();
	
	private ApplicationContext applicationContext = null;
	 
	private com.google.common.cache.LoadingCache<ScriptKey, Object> scriptServiceCache = null;

	@Inject
	@Qualifier("repository")
	private Repository repository;
	
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	/** Map from bean name String to ScriptSource object */
	private final Map<String, ScriptSource> scriptSourceCache = new HashMap<String, ScriptSource>();
	
	private File scriptDir;	
	
	public CommunityGroovyService() {
		
	} 
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void initialize(){		
		log.debug("creating script service cache ...");		
		scriptServiceCache = CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess(1, TimeUnit.MINUTES ).build( 
			new CacheLoader<ScriptKey, Object>(){			
				public Object load(ScriptKey key) throws Exception {
					log.debug("creating new refreshable script service.");		
					return getService(key.location, key.requiredType);
				}
			} 
		);
	}	
	
 
	public void afterPropertiesSet() throws Exception {
		initialize();
	} 
	
	protected synchronized File getScriptDir() {
		if(scriptDir == null)
        {
			scriptDir = repository.getFile("groovy-script");
			if(!scriptDir.exists())
            {
                boolean result = scriptDir.mkdir();
                if(!result)
                	log.error((new StringBuilder()).append("Unable to create directory: '").append(scriptDir).append("'").toString());
            }
        }
        return scriptDir;
	}
	
	
	/**
	 * return resource.
	 * @param path
	 * @return
	 */
	protected Resource getResource(String path) { 
		File file = new File(getScriptDir(), path );
		FileSystemResource resource = new FileSystemResource( file ) ;
		return resource; 
	}
	
	
	public ScriptSource getScriptSource( String scriptSourceLocator) {
		synchronized (this.scriptSourceCache) {
			ScriptSource scriptSource = this.scriptSourceCache.get(scriptSourceLocator);
			if (scriptSource == null) {
				scriptSource = new ResourceScriptSource( getResource(scriptSourceLocator) ) ;
				this.scriptSourceCache.put(scriptSourceLocator, scriptSource);
			}
			return scriptSource;
		}
	} 
	
	private final Map<String, GroovyScriptFactory> groovyScriptFactories = new ConcurrentHashMap<String, GroovyScriptFactory>(100);
	
	public GroovyScriptFactory getGroovyScriptFactory(String scriptSourceLocator, boolean autowire) {
		GroovyScriptFactory factory = groovyScriptFactories.get(scriptSourceLocator);
		if( factory == null) {
			factory = new GroovyScriptFactory(scriptSourceLocator);
			if( autowire)
				applicationContext.getAutowireCapableBeanFactory().autowireBean(factory);
			groovyScriptFactories.put(scriptSourceLocator, factory);
		}
		return factory;
	}
	
	public GroovyScriptFactory getGroovyScriptFactory(String scriptSourceLocator) {
		return getGroovyScriptFactory(scriptSourceLocator, false);
	}
	

	public Class<?> getScriptedObjectType(ScriptSource scriptSource, GroovyScriptFactory factory) throws ScriptCompilationException, IOException {
		return factory.getScriptedObjectType(scriptSource);
	}
	
	public Object getScriptedObject(ScriptSource scriptSource, Class<?> handlerType, GroovyScriptFactory factory, boolean autowire) throws ScriptCompilationException, IOException {
		Object object = factory.getScriptedObject(scriptSource, handlerType);
		if( autowire)
			applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
		return object;
	}

	
	public <T> T getService( String scriptSourceLocator, Class<T> requiredType, boolean usingCache, boolean autowire) { 
		GroovyScriptFactory factory = getGroovyScriptFactory(scriptSourceLocator);
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBean(factory); 
		
		ScriptSource scriptSource = new ResourceScriptSource( getResource(scriptSourceLocator) ); 
		try {  
			Class<?> scriptSourceClassType = factory.getScriptedObjectType(scriptSource);
			T obj = (T)factory.getScriptedObject(scriptSource, requiredType);	
			
			if( autowire)
				applicationContext.getAutowireCapableBeanFactory().autowireBean(obj);
			
			return obj;
		} catch (Exception e) { 
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	
	public <T> T getService(String locator, Class<T> requiredType, boolean refreshable ) {	
		if( refreshable )
			try {
				ScriptKey key = new ScriptKey( locator, requiredType );
				T service =  (T) scriptServiceCache.getIfPresent(key);
				if( service == null )
					service = (T) scriptServiceCache.get(key);
				return service;
			} catch (ExecutionException e) {
				throw new RuntimeError(e);
			}
		else
			return getService(locator, requiredType);
	} 
	
	
	
	
	public <T> T getService( String scriptSourceLocator, Class<T> requiredType) { 
		GroovyScriptFactory factory = getGroovyScriptFactory(scriptSourceLocator);
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBean(factory); 
		ScriptSource scriptSource = new ResourceScriptSource( getResource(scriptSourceLocator) ); 
		try {  
			T obj = (T)factory.getScriptedObject(scriptSource, requiredType);
			applicationContext.getAutowireCapableBeanFactory().autowireBean(obj);
			return obj;
		} catch (Exception e) { 
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public <T> T getRefreshableService(String scriptSourceLocator, Class<T> requiredType) {  
		
		log.debug("load freshable script {} - {}", scriptSourceLocator, requiredType.getName()); 
		GroovyScriptFactory factory = getGroovyScriptFactory(scriptSourceLocator);
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBean(factory); 
		ResourceScriptSource script = new ResourceScriptSource(getResource(scriptSourceLocator));
		RefreshableScriptTargetSource rsts = getRefreshableScriptTargetSource(beanFactory, "groovy_script_servcie____", factory , script, false); 
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTargetSource(rsts);
		proxyFactory.setInterfaces(requiredType); 
		DelegatingIntroductionInterceptor introduction = new DelegatingIntroductionInterceptor(rsts);
		introduction.suppressInterface(TargetSource.class); 
		proxyFactory.addAdvice(introduction);  
		T obj =  (T) proxyFactory.getProxy(); 
		applicationContext.getAutowireCapableBeanFactory().autowireBean(obj);
		
		return obj;	
	}
	
	
	
	protected RefreshableScriptTargetSource getRefreshableScriptTargetSource(BeanFactory beanFactory, String beanName, ScriptFactory scriptFactory, ScriptSource scriptSource, boolean isFactoryBean) {  
		RefreshableScriptTargetSource rsts = new RefreshableScriptTargetSource(beanFactory, beanName, scriptFactory, scriptSource, isFactoryBean) {
			protected Object obtainFreshBean(BeanFactory beanFactory, String beanName) {
				/*
				 * we ask the factory to create a new script bean directly instead
				 * asking the beanFactory for simplicity. 
				 */
				try {
					return scriptFactory.getScriptedObject(scriptSource);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		rsts.setRefreshCheckDelay(1000L);	 
		return rsts;
	} 
	

	
	static class ScriptKey implements Serializable {
		
		String location ;
		Class<?> requiredType ; 
		boolean refreshable;
		
		public ScriptKey(String scriptSourceLocator, Class<?> requiredType) { 
			this.location = scriptSourceLocator;
			this.requiredType = requiredType;
			this.refreshable = true;
		}

		public String getScriptSourceLocator() {
			return location;
		} 
		
		public void setScriptSourceLocator(String scriptSourceLocator) {
			this.location = scriptSourceLocator;
		} 

		public Class<?> getRequiredType() {
			return requiredType;
		} 

		public void setRequiredType(Class<?> requiredType) {
			this.requiredType = requiredType;
		} 

 
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((location == null) ? 0 : location.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ScriptKey other = (ScriptKey) obj;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			return true;
		}

		public boolean equals(ScriptKey obj) {
			if(StringUtils.equals(location, obj.location) && requiredType == obj.requiredType)
				return true;
			return true;
		}
	}

}
