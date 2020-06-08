package architecture.community.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.security.spring.userdetails.CommunityUserDetailsService;
import architecture.community.security.spring.userdetails.SystemUser;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.ee.util.StringUtils;

public class SecurityHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityHelper.class);

	public static final User ANONYMOUS = new UserTemplate(-1L, "ANONYMOUS");
	
	public static final CommuintyUserDetails ANONYMOUS_USER_DETAILS = new CommuintyUserDetails(ANONYMOUS);
	
	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static CommuintyUserDetails getUserDetails() {
		try {
		    Authentication authen = getAuthentication();
		    Object obj = authen.getPrincipal();
		    if (obj instanceof CommuintyUserDetails)
			return ((CommuintyUserDetails) obj);
		} catch (Exception ignore) {
			
		}
		return ANONYMOUS_USER_DETAILS ;
	}
	
	public static CommuintyUserDetails getUserDetails(Authentication authentication) {	 
		try { 
		    Object obj = authentication.getPrincipal();
		    if (obj instanceof CommuintyUserDetails)
			return ((CommuintyUserDetails) obj);
		} catch (Exception ignore) {
			
		}
		return ANONYMOUS_USER_DETAILS ;
	}

	public static User getUser(Authentication authentication) {
		try {
		    Object obj = authentication.getPrincipal();
		    if (obj instanceof CommuintyUserDetails)
		    	return ((CommuintyUserDetails) obj).getUser();
		    else if (obj instanceof SystemUser)
		    	return (SystemUser)obj;
		} catch (Exception ignore) {
			
		}
		return ANONYMOUS;
	}
	
	public static User getUser() {
		try {
		    Authentication authen = getAuthentication();
		    Object obj = authen.getPrincipal();
		    if (obj instanceof CommuintyUserDetails)
		    	return ((CommuintyUserDetails) obj).getUser();
		    else if (obj instanceof SystemUser)
		    	return (SystemUser)obj;
		} catch (Exception ignore) {
			
		}
		return ANONYMOUS;
	}
	
	private static boolean isGranted(String role) {
		if( StringUtils.isEmpty(role)){
			return true;
		}
		
		Authentication auth = getAuthentication();
		if ((auth == null) || (auth.getPrincipal() == null)) {
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		if (authorities == null) {
			return false;
		}
		for (GrantedAuthority grantedAuthority : authorities) {
			if (role.equals(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 인자로 전달된 role 을 현재 사용자가 가지고 있는지 여부를 확인.
	 * 
	 * @param roles
	 * @return
	 */
	public static boolean isUserInRole(String roles) {
		logger.debug("is user in roles : {}", roles);
		boolean flag = false;
		boolean returnFlag = false;
		if( StringUtils.isEmpty(roles)){
			return true;
		}
		if( !StringUtils.isNullOrEmpty(roles)){
			for(String token : StringUtils.tokenizeToStringArray(roles, ",")){
				flag = isGranted(token);
				logger.debug("is granted  {} : {}", token, flag);
				if(flag == true){
					return true;
				}
			}
		}
		return returnFlag;
	}

	
	/**
	 * 익명 사용자 여부를 확인 
	 * 
	 * @return
	 */
	public static boolean isAnonymous(){
		if( getAuthentication()!=null && getAuthentication().isAuthenticated() && ! (getAuthentication() instanceof AnonymousAuthenticationToken) )
			return false;
		else 
			return true;
	}
	
	
	
	/**
	 * 인증 토큰 정보를 새롭게 갱신한다.
	 * 
	 */
	public static void refreshUserToken(){
		User user = getUser();		
		if( !user.isAnonymous() ){
			CommunityUserDetailsService detailsService = CommunityContextHelper.getComponent(CommunityUserDetailsService.class);
			UserDetails details = detailsService.loadUserByUsername(user.getUsername());
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( details, null , details.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(token);
		}		
	}
	
}