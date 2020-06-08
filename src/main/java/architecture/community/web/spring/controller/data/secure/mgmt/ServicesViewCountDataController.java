package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.query.CustomQueryService;
import architecture.community.util.CommunityConstants;
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.model.ServicesConfig;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-services-viewcounts-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/viewcounts")
public class ServicesViewCountDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("repository")
	private Repository repository;	
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired(required = false) 
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	public ServicesViewCountDataController() { 
	
	}
  
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/refresh.json", method = { RequestMethod.POST })
	@ResponseBody
	public Result refresh (NativeWebRequest request) throws NotFoundException { 
		
		if(viewCountService.isViewCountsEnabled())
			viewCountService.updateViewCounts();
		return Result.newResult();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config.json", method = { RequestMethod.POST })
	@ResponseBody
	public ServicesConfig getConfig (NativeWebRequest request) throws NotFoundException { 
		boolean enabled = configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false);
		ServicesConfig config = new ServicesConfig();
		config.setEnabled(enabled);
		return config;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
	public ServicesConfig saveOrUpdate (
			@RequestBody  ServicesConfig config, 
			NativeWebRequest request) throws NotFoundException { 

		log.debug("viewcounts : {}", config.isEnabled());
		configService.setApplicationProperty( CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, Boolean.toString(config.isEnabled()));
		
		return config;
	}	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/stats.json", method = { RequestMethod.POST })
	@ResponseBody
	public ItemList getStats (
			@RequestBody DataSourceRequest dataSourceRequest, 
			NativeWebRequest request) throws NotFoundException { 
 
		boolean enabled = configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false);
		if( enabled ) {
			
		} 
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_VIEWCOUNT_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class); 
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_VIEWCOUNT_REQUEST");
		List<Map<String, Object>> items = customQueryService.list(dataSourceRequest); 
		return new ItemList(items, totalCount );
	}	
		
	

}
