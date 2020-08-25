package architecture.community.web.spring.controller.data.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;

public class SecurityConfig {
	
	boolean allowPageSignup;
	boolean allowPageSignin;
	boolean loginRequired;
	int userRegistration ;
	int passwordMinLength;
	List<String> passwordRequiredCharClasses ;
	int passwordMaxAge = 0 ;
	
	
	public SecurityConfig() {
		allowPageSignup = false;
		allowPageSignin = false;
		loginRequired = false;
		userRegistration = 0;
		passwordMinLength = 8;
		passwordRequiredCharClasses = new ArrayList<String>();
		passwordMaxAge = 0 ;
	} 
	
	public boolean isAllowPageSignup() {
		return allowPageSignup;
	}

	public void setAllowPageSignup(boolean allowPageSignup) {
		this.allowPageSignup = allowPageSignup;
	}

	public boolean isAllowPageSignin() {
		return allowPageSignin;
	}

	public void setAllowPageSignin(boolean allowPageSignin) {
		this.allowPageSignin = allowPageSignin;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public void setLoginRequired(boolean loginRequired) {
		this.loginRequired = loginRequired;
	}

	public int getUserRegistration() {
		return userRegistration;
	}

	public void setUserRegistration(int userRegistration) {
		this.userRegistration = userRegistration;
	}

	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	public List<String> getPasswordRequiredCharClasses() {
		return passwordRequiredCharClasses;
	}

	public void setPasswordRequiredCharClasses(List<String> passwordRequiredCharClasses) {
		this.passwordRequiredCharClasses = passwordRequiredCharClasses;
	}

	public int getPasswordMaxAge() {
		return passwordMaxAge;
	}

	public void setPasswordMaxAge(int passwordMaxAge) {
		this.passwordMaxAge = passwordMaxAge;
	}

	public static class Builder {  
		
		private SecurityConfig settings;
		
		public Builder(){
			this.settings = new SecurityConfig();
		}
		
		public Builder(SecurityConfig settings){
			this.settings = settings;
		}
		
		public Builder(ConfigService configService) { 
			settings = new SecurityConfig(); 
			settings.allowPageSignup = configService.getApplicationBooleanProperty("settings.security.allowPageSignup", false);
			settings.allowPageSignin = configService.getApplicationBooleanProperty("settings.security.allowPageSignin", false);
			settings.loginRequired = configService.getApplicationBooleanProperty("settings.security.loginRequired", false);
			settings.userRegistration = configService.getApplicationIntProperty("settings.security.userRegistration", 0);
			settings.passwordMinLength = configService.getApplicationIntProperty("settings.security.passwordMinLength", 8);
			settings.passwordMaxAge = configService.getApplicationIntProperty("settings.security.passwordMaxAge", 8); 
			settings.passwordRequiredCharClasses = CollectionUtils.arrayToList(  
				StringUtils.split(configService.getApplicationProperty("settings.security.passwordRequiredCharClasses", ""), ",")
			);
		}
		public SecurityConfig build() {
			return settings;
		}
		
		public SecurityConfig save(ConfigService configService) { 
			configService.setApplicationProperty("settings.security.allowPageSignup", Boolean.toString( settings.allowPageSignup )); 
			configService.setApplicationProperty("settings.security.allowPageSignin", Boolean.toString( settings.allowPageSignin )); 
			configService.setApplicationProperty("settings.security.loginRequired", Boolean.toString( settings.loginRequired )); 
			configService.setApplicationProperty("settings.security.userRegistration", Integer.toString( settings.userRegistration )); 
			configService.setApplicationProperty("settings.security.passwordMinLength", Integer.toString( settings.passwordMinLength )); 
			configService.setApplicationProperty("settings.security.passwordMaxAge", Integer.toString( settings.passwordMaxAge ));  
			configService.setApplicationProperty("settings.security.passwordRequiredCharClasses", StringUtils.collectionToDelimitedString(settings.passwordRequiredCharClasses, ",")); 
			return settings;
		}
		
	}
}
