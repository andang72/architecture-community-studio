package architecture.community.web.spring.controller.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.security.spring.authentication.jwt.JwtTokenProvider;
import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserTemplate;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.model.JwtResponse;
import architecture.community.web.spring.controller.data.model.LoginRequest;
import architecture.ee.service.ConfigService;

@Controller("accounts-data-controller")
@RequestMapping("/data/accounts")
public class AccountsDataController {

	private Logger logger = LoggerFactory.getLogger(getClass());	
	
	@Autowired(required=false)
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required=false)
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;
	
	@Autowired(required=false)
	@Qualifier("jwtTokenProvider")
	private JwtTokenProvider jwtTokenProvider;
	
	
	@Autowired(required = false)
	private UserManager userManager;
	
	
	@RequestMapping(value = "/signin.json", method = { RequestMethod.POST})
	public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) { 
		
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtTokenProvider.createToken(authentication); 
		CommuintyUserDetails details = SecurityHelper.getUserDetails(authentication);  
		return ResponseEntity.ok( new JwtResponse(jwt, details.getUser(), getRoles(details.getAuthorities())));
		
	}
	
	protected List<String> getRoles(Collection<GrantedAuthority> authorities) {
		List<String> list = new ArrayList<String>();
		for (GrantedAuthority auth : authorities) {
		    list.add(auth.getAuthority());
		}
		return list;
	}

	
	@RequestMapping(value = "/signup-with-data.json", method = { RequestMethod.POST})
	@ResponseBody
	public Object signupByJson(@RequestBody DataSourceRequest data, NativeWebRequest request) {
		
		String nameToUse =  data.getDataAsString("name", null);
		String emailToUse =  data.getDataAsString("email", null);
		String usernameToUse ;
		if( data.getData().containsKey("username") && StringUtils.isNotEmpty(  data.getDataAsString("username", null)  )) {
			usernameToUse = data.getDataAsString("username", null);
		}else {
			usernameToUse = extractUsernameFromEmail(emailToUse);
		} 
		
		String passwordToUse =  data.getDataAsString("password", null);
		boolean mameVisible =  data.getDataAsBoolean("nameVisible", false);
		boolean emailVisible =  data.getDataAsBoolean("emailVisible", false);	
		String ipAddress = request.getNativeRequest(HttpServletRequest.class).getRemoteAddr();		 

		User newUser = new UserTemplate(usernameToUse, passwordToUse, nameToUse, mameVisible, emailToUse, emailVisible); 
		Result result = Result.newResult();	
		result.setAnonymous(true);		
		
		try {
			User user = userManager.createUser(newUser);			
			result.setCount(1);
			result.setSuccess(true);
			result.getData().put("user", user);
		} catch (Exception e) {			
			result.setError(e);
		}
		return result;	
	}
	
	
	/**
	 * this signup required user form data.
	 * 
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/signup-with-user.json", method = { RequestMethod.POST})
	@ResponseBody
	public Result signup(@RequestBody UserForm user, NativeWebRequest request)  {		
		Result result = Result.newResult();	
		result.setAnonymous(true);	
		logger.debug(user.getUsername());
		logger.debug(user.getName());
		logger.debug(user.getEmail()); 
		
		String usernameToUse = user.getUsername();
		if( StringUtils.isEmpty( usernameToUse  ))
			usernameToUse = user.getEmail();
		try {
			
			User newUser = new UserTemplate(usernameToUse, user.password, user.name, user.mameVisible, user.email, user.emailVisible);				
			User registeredUser = userManager.createUser(newUser);
			result.getData().put("user", registeredUser);
			result.setCount(1);
			
		} catch (Exception e) {			
			result.setError(e);
		}
		return result;	
	}
	
	/**
	 * remove last of @ string from emal address.
	 * 
	 * @param email
	 * @return
	 */
	private String extractUsernameFromEmail(String email){		
		int index = email.indexOf('@');
		return email.substring(0, index );
	}
	
	
	private static class UserForm  { 
		private String username ;
		private String name ;
		private String password;
		private String email;
		private Boolean mameVisible;
		private Boolean emailVisible; 
		
		public UserForm() {
			mameVisible = false;
			emailVisible = false;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}

		public Boolean getMameVisible() {
			return mameVisible;
		}
		public void setMameVisible(Boolean mameVisible) {
			this.mameVisible = mameVisible;
		}
		public Boolean getEmailVisible() {
			return emailVisible;
		}
		public void setEmailVisible(Boolean emailVisible) {
			this.emailVisible = emailVisible;
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("UserForm [");
			if (name != null)
				builder.append("name=").append(name).append(", ");
			if (password != null)
				builder.append("password=").append(password).append(", ");
			if (email != null)
				builder.append("email=").append(email);
			builder.append("]");
			return builder.toString();
		} 
		
	}
}
