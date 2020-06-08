package architecture.community.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class CommunitySpringEventPublisher {

	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	public CommunitySpringEventPublisher() {  
	}

	public void fireEvent(Object event){		
		if( applicationEventPublisher != null ){
			applicationEventPublisher.publishEvent(event);
		}
	}
	
}
