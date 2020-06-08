package architecture.community.security.spring.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.eventbus.EventBus;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.user.UserManager;
import architecture.community.user.event.UserActivityEvent;

public class CommunityAuthenticationProvider extends DaoAuthenticationProvider implements ApplicationEventPublisherAware {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("userManager")
	private UserManager userManager;	
	
	@Autowired(required = false)
	@Qualifier("eventBus")
	private EventBus eventBus;
	
	private ApplicationEventPublisher applicationEventPublisher;	
	
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null)
		    throw new BadCredentialsException(CommunityLogLocalizer.getMessage("010101")); 
		
		checkLicense(userDetails.getUsername());  
		try {
			CommuintyUserDetails user = (CommuintyUserDetails) userDetails;
			if( user.getUser().isExternal() )
			{
				logger.debug("This is external : {}. Auth not supported yet for external users.", user.getUsername() ); 
			}
			
			UserActivityEvent event = new UserActivityEvent(this, user.getUser(), UserActivityEvent.ACTIVITY.SIGNIN );
			if(applicationEventPublisher!= null) {
				applicationEventPublisher.publishEvent( event );
			}
			
			if(eventBus!=null){
				eventBus.post(event);
			}
		} catch (Exception e) {
		    logger.error(CommunityLogLocalizer.getMessage("010102"), e);
		    throw new BadCredentialsException( messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		} 
		logger.debug("additional authentication checks");
		super.additionalAuthenticationChecks(userDetails, authentication); 
	}
	
	private void checkLicense ( String username ) {
		
		
	}
}