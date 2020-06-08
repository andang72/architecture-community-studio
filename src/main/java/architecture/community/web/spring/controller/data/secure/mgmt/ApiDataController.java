package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import architecture.community.page.api.Api;
import architecture.community.page.api.ApiNotFoundException;
import architecture.community.page.api.ApiService;
import architecture.community.query.CustomQueryService;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.user.EmailAlreadyExistsException;
import architecture.community.user.User;
import architecture.community.user.UserAlreadyExistsException;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;

@Controller("community-mgmt-api-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ApiDataController {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired( required = false) 
	@Qualifier("apiService")
	private ApiService apiService;
	
	@Autowired( required = false) 
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired( required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("aclService")
	private CommunityAclService communityAclService;
	

	/**
	 * API API 
	******************************************/ 
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getApis (@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {

		dataSourceRequest.setStatement("COMMUNITY_PAGE.COUNT_SERVICE_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		
		dataSourceRequest.setStatement("COMMUNITY_PAGE.SELECT_SERVICE_IDS_BY_REQUEST");
		
		List<Long> ids = customQueryService.list(dataSourceRequest, Long.class);
		List<Api> apis = new ArrayList<Api>(ids.size());
		for( Long apiId : ids ) {
			try {
				apis.add(apiService.getApiById(apiId));
			} catch (ApiNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}		
		return new ItemList(apis, totalCount);	
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result updateApi(@RequestBody Api api , NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException, EmailAlreadyExistsException { 
		log.debug("Save or update api {} ",  api );
		
		User user = SecurityHelper.getUser();
		Api apiToUse = api ;
		
		if( apiToUse.getApiId() <= 0 ) {
			apiToUse.setCreator(user );
		}
		apiService.saveOrUpdate(apiToUse);
		return Result.newResult("item", apiToUse);
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/{apiId:[\\p{Digit}]+}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Api getApi(@PathVariable Long apiId, NativeWebRequest request) throws NotFoundException {
		User user = SecurityHelper.getUser();
		Api api = apiService.getApiById(apiId);
		return api ;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/{apiId:[\\p{Digit}]+}/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result deleteApi(@PathVariable Long apiId, NativeWebRequest request) throws NotFoundException {
		User user = SecurityHelper.getUser();
		Api api = apiService.getApiById(apiId);
		apiService.deleteApi(api);
		return Result.newResult("item", api);
	}	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/{apiId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getRESTfulAPIProperties (
		@PathVariable Long apiId, 
		NativeWebRequest request) throws NotFoundException {
		Api api = apiService.getApiById(apiId);
		Map<String, String> properties = api.getProperties(); 
		return toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/{apiId:[\\p{Digit}]+}/properties/update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> updateRESTfulAPIProperties (
		@PathVariable Long apiId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		Api api = apiService.getApiById(apiId);
		Map<String, String> properties = api.getProperties();   
		// update or create
		for (Property property : newProperties) {
		    properties.put(property.getName(), property.getValue().toString());
		} 
		apiService.saveOrUpdate(api); 
		return toList(apiService.getApiById(apiId).getProperties());
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/apis/{apiId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> deleteRESTfulAPIProperties (
		@PathVariable Long apiId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		
		Api api = apiService.getApiById(apiId);
		Map<String, String> properties = api.getProperties();  
		for (Property property : newProperties) {
		    properties.remove(property.getName());
		}
		apiService.saveOrUpdate(api); 
		return toList(apiService.getApiById(apiId).getProperties());
	}	
	
	protected List<Property> toList(Map<String, String> properties) {
		List<Property> list = new ArrayList<Property>();
		for (String key : properties.keySet()) {
			String value = properties.get(key);
			list.add(new Property(key, value));
		}
		return list;
	}		
}
