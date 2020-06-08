package architecture.community.web.spring.controller.page;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;

import architecture.community.audit.event.AuditLogEvent;
import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;

@Controller("community-secure-display-controller")
@RequestMapping("/secure/display")
public class SecurePageController {

	private static final Logger log = LoggerFactory.getLogger(SecurePageController.class);
	
	@Autowired(required=false)
    @Qualifier("configService")
    private ConfigService configService;
	
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	private boolean isSetConfigService() { 
		return configService != null;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_OPERATOR"})
	@RequestMapping(value = "/ftl/**", method = { RequestMethod.POST, RequestMethod.GET })
    public String page(
	    HttpServletRequest request, 
	    HttpServletResponse response, 
	    Model model) throws NotFoundException, IOException {		
		ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);	
		String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);		
		String lcStr = restOfTheUrl.substring(19).toLowerCase();
		
		log.debug("view {} > {} .", restOfTheUrl, lcStr );
		
		if(communitySpringEventPublisher!=null)
			communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
						.objectTypeAndObjectId(Models.PAGE.getObjectType(), -1)
						.action(AuditLogEvent.READ)
						.code(this.getClass().getName())
						.resource(lcStr).build()); 
		
		return lcStr;
    }
	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_OPERATOR"})
	@RequestMapping(value = "/{filename:.+}", method = { RequestMethod.POST, RequestMethod.GET })
    public String page(@PathVariable String filename, 
    		@RequestParam(value = "t", required = false) String template, 
    		@RequestParam(value = "source", required = false) String view, 
	    HttpServletRequest request, 
	    HttpServletResponse response, 
	    Model model) 
	    throws NotFoundException, IOException {
	
		ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
		User user = SecurityHelper.getUser();		
		
		if( StringUtils.isEmpty(view)){	    
		    view = configService.getLocalProperty("view.html.page.secure.main");
		}
		
		if( !StringUtils.isEmpty(template)){
			view = template;
		}
		
		if(communitySpringEventPublisher!=null)
			communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
					.objectTypeAndObjectId(Models.PAGE.getObjectType(), -1)
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.resource(view).build()); 
		
		return view;
    }
}
