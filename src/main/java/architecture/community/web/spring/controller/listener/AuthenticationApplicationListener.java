package architecture.community.web.spring.controller.listener;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;

public class AuthenticationApplicationListener {

	public AuthenticationApplicationListener() {
	}
 
	@EventListener
	public void handleSessionDestroyedEvent(SessionDestroyedEvent event) {
		List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
		for (SecurityContext securityContext : lstSecurityContext) {
			
			// Try to find out, if this event is caused by a logout,
			// This is true, when the old session has been an authenticated one.
			Authentication auth = securityContext.getAuthentication();
			if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
				return;
			} 
			// do something
		}
	}
}
