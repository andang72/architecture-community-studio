package architecture.community.user.event.listener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import architecture.community.user.event.UserActivityEvent;
import architecture.ee.service.ConfigService;

@Component
public class UserActivityEventListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	@PostConstruct
	public void initialize() throws Exception {
		if( configService != null)
		{
			//configService.registerEventListener(this);
		}
	}
	
	@PreDestroy
	public void destory(){
		if( configService != null)
		{ 
		}
	}
	
	@Subscribe 
	@EventListener 
	@Async
	public void handelUserActivityEvent(UserActivityEvent e) {
		
		logger.debug("USER : {}, ACTIVITY:{}" , e.getUser().getUsername(), e.getActivity().name() );
	}
	
}