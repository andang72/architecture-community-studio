package architecture.community.web.spring.controller.advice;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.security.spring.userdetails.SystemUser;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;

@ControllerAdvice
public class CurrentUserControllerAdvice {

	@ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        return (authentication == null) ? SecurityHelper.ANONYMOUS :loadUser(authentication.getPrincipal()) ;
    }

	private User loadUser(Object obj) {
		User user = SecurityHelper.ANONYMOUS ;
		if( obj != null ) {
			if(obj instanceof CommuintyUserDetails ) {
				user = ((CommuintyUserDetails) obj).getUser();
			}
			else if ( obj instanceof SystemUser ) {
				user = (SystemUser) obj;
			}
		}
		return user;
	}	
	
}
