package architecture.community.security.spring.userdetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import architecture.community.user.Role;
import architecture.community.user.RoleManager;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.CommunityConstants;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;
public class CommunityUserDetailsService implements UserDetailsService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;	
	
	@Inject
	@Qualifier("roleManager")
	private RoleManager roleManager;	
	
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;	
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {			
			logger.debug("loading user by {}", username );
			User user = userManager.getUser(username); 
			logger.debug("loaded user {}", user );
			CommuintyUserDetails details = new CommuintyUserDetails(user, getFinalUserAuthority(user)); 
			return details ;			
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException("User not found.", e);	
		}
	}

	protected List<GrantedAuthority> getFinalUserAuthority(User user) {		
		if( user.getUserId() <= 0)
			return Collections.EMPTY_LIST;
		
		String authority = configService.getLocalProperty(CommunityConstants.SECURITY_AUTHENTICATION_AUTHORITY_PROP_NAME);
	    if(logger.isDebugEnabled())
	    	logger.debug("grant default authentication {}", authority );
		List<String> roles = new ArrayList<String>();		
		if(! StringUtils.isNullOrEmpty( authority ))
		{
			authority = authority.trim();
		    if (!roles.contains(authority)) {
		    	roles.add(authority);
		    }
		}
		for(Role role : roleManager.getFinalUserRoles(user.getUserId())){
			roles.add(role.getName());
		}
		return AuthorityUtils.createAuthorityList(StringUtils.toStringArray(roles));
	}
	
}
