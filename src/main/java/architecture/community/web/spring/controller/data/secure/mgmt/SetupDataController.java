package architecture.community.web.spring.controller.data.secure.mgmt;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
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

import architecture.community.model.Property;
import architecture.community.services.CommunityAdminService;
import architecture.community.services.setup.CommunitySetupService;
import architecture.community.web.model.Result;
import architecture.ee.component.editor.DataSourceConfigReader;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository; 

@Controller("community-mgmt-setup-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class SetupDataController {
	
	private Logger log = LoggerFactory.getLogger(SetupDataController.class);
	
	@Autowired( required = true) 
	@Qualifier("repository")
	private Repository repository;
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired
	@Qualifier("adminService")
	CommunityAdminService adminService;
	
	@Inject
	@Qualifier("setupService")
	private CommunitySetupService setupService;
	
	public SetupDataController() { 
	
	}

	private DataSourceConfigReader getDataSourceConfigReader() {
		return new DataSourceConfigReader(repository.getSetupApplicationProperties());
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/setup/menu/reload.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result reloadSetupMenu( NativeWebRequest request){  
		Result result = Result.newResult(); 
		log.debug("SETUP Menu Reloading...");
		adminService.reloadMenu();
		log.debug("Menu Reloaded.");
		return result;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/setup/datasource/deploy.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result setupDatasources( NativeWebRequest request){  
		Result result = Result.newResult(); 
		log.debug("SETUP DataSource Deploy...");
		setupService.setupDataSources();
		log.debug("DataSource Deployed.");
		return result;
	}	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/setup/database/init.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result setupDatabase( NativeWebRequest request){  
		Result result = Result.newResult();
			
		log.debug("SETUP Database Init...");
		if( configService.isSetDataSource() && !configService.isDatabaseInitialized())
		{
			log.debug("Database Initializing.");
			setupService.setupDatabase();
		}
		return result;
	}
	 
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/setup/property/update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result setProperty( 
		@RequestBody Property property, 
		NativeWebRequest request){  
		Result result = Result.newResult();
		
		log.debug("update propery {} = {}", property.getName(), property.getValue());
		if( StringUtils.isNotEmpty(property.getName()))
			configService.setApplicationProperty(property.getName(), property.getValue());
		 
		return result;
	}
	
	
}
