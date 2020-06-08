package architecture.community.audit.event;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.util.CommunityConstants;
import architecture.ee.service.ConfigService;

public abstract class AbstractAuditTrailsService implements AuditTrailsService , InitializingBean {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("configService")
	ConfigService configService;	
	
	@Autowired(required=false)
	@Qualifier("customQueryJdbcDao")
	protected CustomQueryJdbcDao customQueryJdbcDao ;
	
	public boolean isEnabled() {
		return configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, false);
	}	
	
	public void afterPropertiesSet() throws Exception {  
		
	}
	
}
