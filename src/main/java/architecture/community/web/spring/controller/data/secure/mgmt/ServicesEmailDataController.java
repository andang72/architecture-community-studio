package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Property;
import architecture.community.query.CustomQueryService;
import architecture.community.services.mail.CommunityMailService;
import architecture.community.services.mail.MailServiceConfigEditor;
import architecture.community.services.mail.MailServicesConfig;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.Utils;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.util.StringUtils;
import architecture.studio.components.email.SendBulkEmail;
import architecture.studio.components.templates.DefaultTemplates;
import architecture.studio.components.templates.Templates;
import architecture.studio.services.SendBulkEmailService;

@Controller("community-mgmt-services-email-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/mail")
public class ServicesEmailDataController {
	
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
	
	@Autowired(required=false)
	@Qualifier("simpleEmailSendSerivce")
	private SendBulkEmailService simpleEmailSendSerivce;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	public ServicesEmailDataController() { 
		
	}
	
	/**
	 * EMAIL API 
	******************************************/	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = { "/config.json", "/configuration" }, method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public MailServicesConfig getMailServicesConfig(
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		log.debug("Is set mail service : {} {}", mailService!=null ? true : false , mailService);
		MailServicesConfig config = mailService.getMailServicesConfig(); 
		log.debug("Mail Config : {}", config );
		
		return config; 
    } 
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = { "/test.json", "/verification-configuration" }, method = { RequestMethod.POST, RequestMethod.GET })
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
	@RequestMapping(value = "/save-or-update.json", method = { RequestMethod.POST , RequestMethod.PUT  })
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
		File file = repository.getConfigRoot().getFile(CommunityMailService.DEFAULT_CONFIG_FILENAME);
		MailServiceConfigEditor editor = new MailServiceConfigEditor(file); 
		return editor ;
	}
	
	/**
	 * OUTBOUND EMAIL API 
	******************************************/	
	
	/**
	 * send bulk emails ( save or update..)
	 * @param request
	 * @return 
	 * @throws IOException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"}) 
	@RequestMapping(value = {"/outbound-bulk-emails", "/outbound-bulk-emails.json"}, method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseBody
    public ItemList getSendBulkEmails(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) throws IOException {  
		dataSourceRequest.setStatement("STUDIO_OUTBOUND.COUNT_BULK_EMAILS_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("STUDIO_OUTBOUND.SELECT_BULK_EMAIL_IDS_BY_REQUEST"); 
		List<Long> IDs = customQueryService.list(dataSourceRequest, Long.class); 
		List<SendBulkEmail> items = new ArrayList<SendBulkEmail>(totalCount);
		for( Long sendEmailId : IDs ) {
			try {
				SendBulkEmail sbe = simpleEmailSendSerivce.getSendBulkEmail(sendEmailId);
				items.add(sbe); 
			} catch (NotFoundException e) {
			}
		} 
		return new ItemList(items, totalCount ); 
    } 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"}) 
	@RequestMapping(value = {"/outbound-bulk-emails/{sendEmailId:[\\p{Digit}]+}", "/outbound-bulk-emails/{sendEmailId:[\\p{Digit}]+}/save-or-update.json"}, method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseBody
    public Result saveOrUpdateSendBulkEmails(@RequestBody SendBulkEmail emails, @PathVariable Long sendEmailId, NativeWebRequest request) throws NotFoundException {  
		
		Result result = Result.newResult();
		
		User me = SecurityHelper.getUser();
		
		SendBulkEmail toUse = emails;
		if(toUse.getEmailId() > 0  ) { 
			
			toUse = simpleEmailSendSerivce.getSendBulkEmail(emails.getEmailId());
			if( !StringUtils.isNullOrEmpty(emails.getFromEmailAddress()) && !StringUtils.equals(toUse.getFromEmailAddress(), emails.getFromEmailAddress()) )
				toUse.setFromEmailAddress( emails.getFromEmailAddress() );
		} 
		toUse.setCreator(me);
		toUse.setModifier(me);   
		simpleEmailSendSerivce.saveOrUpdate(toUse);
		result.getData().put("item", toUse);
		return result;
    } 
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/outbound-bulk-emails/{sendEmailId:[\\p{Digit}]+}"}, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public SendBulkEmail getSendBulkEmailById(@PathVariable Long sendEmailId, NativeWebRequest request) throws NotFoundException {   
		return simpleEmailSendSerivce.getSendBulkEmail(sendEmailId); 
	} 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/templates/{sendEmailId:[\\p{Digit}]+}"}, method = { RequestMethod.DELETE }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result deleteSendBulkEmail(@PathVariable Long sendEmailId, NativeWebRequest request) throws NotFoundException { 
		Result result = Result.newResult();
		SendBulkEmail sbe = simpleEmailSendSerivce.getSendBulkEmail(sendEmailId);
		try {
			simpleEmailSendSerivce.remove(sbe);
		} catch (Exception e) {
			result.setError(e);
		} 
		return result;
	} 
}
