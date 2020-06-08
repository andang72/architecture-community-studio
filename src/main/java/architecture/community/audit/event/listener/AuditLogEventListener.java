package architecture.community.audit.event.listener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import architecture.community.audit.AuditActionContext;
import architecture.community.audit.AuditTrailService;
import architecture.community.audit.event.AuditLogEvent;
import architecture.community.audit.event.AuditTrailsService;

@Component
public class AuditLogEventListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired(required=false)
	@Qualifier("auditTrailsService")
	private AuditTrailsService auditTrailsService;
	
	@Autowired(required=false)
	@Qualifier("auditTrailService")
	private AuditTrailService auditTrailService;
	
	
	@PostConstruct
	public void initialize() throws Exception {

	}
	
	@PreDestroy
	public void destory(){

	}
	
	@Subscribe 
	@EventListener 
	@Async
	public void handelAuditLogEvent(AuditLogEvent e) { 
		
		if(auditTrailsService!=null)
			auditTrailsService.leave(e); 
		
		if(auditTrailService!=null)
			auditTrailService.record(e.getContext());
	}	

	@Subscribe 
	@EventListener 
	@Async
	public void handelAuditLogEvent(AuditActionContext e) { 
		if(auditTrailService!=null)
			auditTrailService.record(e);
	}	
}
