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
import architecture.ee.service.ConfigService;

@Controller("community-mgmt-settings-secure-data-controller")
@RequestMapping("/data/secure/mgmt/settings")
public class SettingDataController {

	private static final Logger log = LoggerFactory.getLogger(ConfigDataController.class);
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	/**
	 * CONFIG API 
	******************************************/
	
	public SettingDataController() { 
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
	@RequestMapping(value = "/properties", method = { RequestMethod.GET },  produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Property> getProperties(NativeWebRequest request){ 
		if(!configService.isDatabaseInitialized())
		{
			return Collections.EMPTY_LIST;
		}
		return getApplicationProperties() ;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/properties", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE )
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
	@RequestMapping(value = "/properties", method = { RequestMethod.DELETE })
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
