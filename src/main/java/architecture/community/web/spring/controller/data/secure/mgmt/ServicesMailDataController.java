package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Property;
import architecture.community.services.mail.CommunityMailService;
import architecture.community.services.mail.MailServiceConfigEditor;
import architecture.community.services.mail.MailServicesConfig;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.Utils;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-services-mail-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/mail")
public class ServicesMailDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("repository")
	private Repository repository;	
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired(required=false)
	@Qualifier("mailService")
	private CommunityMailService mailService; 
	
	public ServicesMailDataController() { 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public MailServicesConfig getMailServicesConfig(
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		log.debug("Is set mail service : {} {}", mailService!=null ? true : false , mailService);
		MailServicesConfig config = mailService.getMailServicesConfig(); 
		log.debug("Mail Config : {}", config );
		
		return config; 
    } 
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/test.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result testConnection(NativeWebRequest request) {   
		Result result = Result.newResult();
		log.debug("Mail Service Enabled  : {}", mailService.isEnabled() );
		if( mailService.isEnabled())
		{
			log.debug("Test Connection .....");
			try {
				mailService.testConnection();
				log.debug("Test Connection Successed.");
			} catch (Exception e) {
				result.setError(e);
			}
		}
		return result; 
    } 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public List<Property> properties(
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		MailServicesConfig config = mailService.getMailServicesConfig(); 
		return Utils.toList(config.getProperties());
    } 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
    public MailServicesConfig saveOrUpdate(
    		@RequestBody MailServicesConfig config, 
    		@RequestParam(value = "restart", defaultValue = "true", required = false) Boolean restart, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		getMailServiceConfigEditor().setMailServicesConfig(config);
		log.debug("Restart mail service : {}", restart);
		mailService.refresh();	
		return config;
    } 
	
	
	
	private MailServiceConfigEditor getMailServiceConfigEditor (){   
		File file = repository.getConfigRoot().getFile(CommunityMailService.CONFIG_FILENAME);
		MailServiceConfigEditor editor = new MailServiceConfigEditor(file); 
		return editor ;
	}
	
	
}
