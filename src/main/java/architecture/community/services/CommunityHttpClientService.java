package architecture.community.services;

import org.springframework.beans.factory.InitializingBean;

import architecture.community.services.support.PooledHttpClientSupport;

public class CommunityHttpClientService extends PooledHttpClientSupport implements InitializingBean , HttpClientService {
 

	public void afterPropertiesSet() throws Exception {
				
	}
	
}
