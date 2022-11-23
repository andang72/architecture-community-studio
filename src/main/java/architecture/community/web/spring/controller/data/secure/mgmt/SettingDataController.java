package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Property;
import architecture.community.web.spring.controller.data.model.SecurityConfig;
import architecture.community.web.spring.controller.data.model.SecurityConfig.Builder;
import architecture.community.web.spring.controller.data.secure.mgmt.LocaleDataController.LocaleBean;
import architecture.ee.service.ConfigService;

@Controller("community-mgmt-settings-secure-data-controller")
@RequestMapping("/data/secure/mgmt/settings")
public class SettingDataController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationPropsDataController.class);
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	
	public SettingDataController() { 
	}
	
	/**
	 * Application Config API 
	******************************************/
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = { "/config", "/config.json"}, method = { RequestMethod.GET },  produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppConfig getConfig(NativeWebRequest request){  
		
		return AppConfig.build(configService);
	} 
	
	/**
	 * Application Property 
	 */
	public static class AppConfig {
		
		private String timezone ;
		
		private LocaleBean locale ; 

		public LocaleBean getLocale() {
			return locale;
		} 

		public String getTimezone() {
			return timezone;
		} 

		public static AppConfig build(ConfigService configService) {
			AppConfig props = new AppConfig();
			
			props.locale = LocaleBean.build(configService.getLocale()); 
			props.timezone = configService.getTimeZone().getID(); 
			
			return props;
		}
	}
	
	
	/**
	 * Security Config API 
	******************************************/
 
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/security", method = { RequestMethod.GET },  produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SecurityConfig getSecuritySettings(NativeWebRequest request){  
		Builder builder = new Builder(); 
		if(configService.isDatabaseInitialized())
		{
			builder = new Builder(configService);
		}
		return builder.build();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/security", method = { RequestMethod.POST , RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public SecurityConfig saveOrUpdate(@RequestBody SecurityConfig config, NativeWebRequest request) throws NotFoundException {  
	
		if(configService.isDatabaseInitialized())
		{
			Builder builder = new Builder(config);
			builder.save(configService);
		} 		
		return config;
	}
	
	
	/**
	 * Properties API 
	******************************************/
	private List<Property> getApplicationProperties(){ 
		List<String> propertyKeys = configService.getApplicationPropertyNames();
		List<Property> list = new ArrayList<Property>(); 
		for( String key : propertyKeys ) {
			String value = configService.getApplicationProperty(key);
			list.add(new Property( key, value ));
		}
		return list ;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/properties", "/properties/list.json" }, method = { RequestMethod.GET },  produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Property> getProperties(NativeWebRequest request){ 
		if(!configService.isDatabaseInitialized())
		{
			return Collections.EMPTY_LIST;
		}
		return getApplicationProperties() ;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = { "/properties" , "/properties/update.json"}, method = { RequestMethod.POST , RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public List<Property> saveOrUpdate(@RequestBody List<Property> newProperties, NativeWebRequest request) throws NotFoundException {  
		if(!configService.isDatabaseInitialized())
		{
			return Collections.EMPTY_LIST;
		} 
		for (Property property : newProperties) {
			configService.setApplicationProperty(property.getName(), property.getValue());
		}		
		return newProperties;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/properties/{key}", method = RequestMethod.GET)
	@ResponseBody
	public Property getProperty( @PathVariable String key, NativeWebRequest request) throws NotFoundException { 
		return new Property(key, configService.getApplicationProperty(key));
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = { "/properties" , "/properties/delete.json"}, method = { RequestMethod.DELETE })
	@ResponseBody
	public List<Property> deleteProperties(@RequestBody List<Property> newProperties, NativeWebRequest request) throws NotFoundException { 
		if(!configService.isDatabaseInitialized())
		{
			return Collections.EMPTY_LIST;
		} 
		for (Property property : newProperties) {
			configService.deleteApplicationProperty(property.getName());
		}
		return getApplicationProperties();
	}
}
