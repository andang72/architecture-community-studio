package architecture.community.services;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;

import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
 
/**
 * 동적으로 Bean 을 Spring context 에 추가하는 서비스...
 * 
 * @author donghyuck
 */
public class CommunitySetupService  implements InitializingBean, ApplicationContextAware  {

	@Autowired
	private ConfigService configService;	
	
	@Autowired
	private Repository repository;
	
	@Autowired(required = false)
	private ApplicationEventPublisher applicationEventPublisher;
	
	private ApplicationContext applicationContext = null;
	
	public CommunitySetupService() {
 
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext ;
	}
 
	public void afterPropertiesSet() throws Exception {

	}
	
	
	

}
