package architecture.community.viewcount.event.listener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.google.common.eventbus.Subscribe;

import architecture.community.viewcount.ViewCountService;
import architecture.community.viewcount.event.ViewCountEvent;
import architecture.ee.service.ConfigService;

public class ViewCountEventListener {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required = false) 
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	@PostConstruct
	public void initialize() throws Exception {
	
	}
	
	@PreDestroy
	public void destory(){
	
	}
	
	@Subscribe 
	@EventListener 
	@Async
	public void handelViewCountEvent(ViewCountEvent e) { 
		logger.debug("VIEW : {} , {} " , e.getObjectType(), e.getObjectId() ); 
		if( viewCountService.isViewCountsEnabled() )
			viewCountService.addViewCount(e.getObjectType(), e.getObjectId()); 
	}
}
