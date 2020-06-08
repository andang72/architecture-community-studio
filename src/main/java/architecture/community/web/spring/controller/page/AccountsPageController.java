package architecture.community.web.spring.controller.page;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import architecture.community.audit.event.AuditLogEvent;
import architecture.community.model.Models;
import architecture.community.page.Page;
import architecture.community.page.PageNotFoundException;
import architecture.community.page.PageService;
import architecture.community.page.PageState;
import architecture.community.services.CommunityGroovyService;
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.util.SecurityHelper;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;

@Controller("accounts-page-controller")
@RequestMapping("/accounts")
public class AccountsPageController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
		
	@Autowired(required=false)
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required=false)
	@Qualifier("pageService")
	private PageService pageService;
	
	private boolean isSetPageService() {
		return pageService != null;
	}
	
	@Autowired(required=false)
	@Qualifier("communityGroovyService")
	private CommunityGroovyService communityGroovyService;	
	
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	@RequestMapping(value={"/signin","/login"}, method = { RequestMethod.POST, RequestMethod.GET } )
    public Object displayLoginPage(
    		@RequestParam(value="redirect_after_login", defaultValue="/", required=false ) String returnUrl,
    		@RequestParam(value="error", defaultValue="false", required=false ) boolean hasError,
    		HttpServletRequest request, 
    		HttpServletResponse response, 
    		Model model) {		
		
		ServletUtils.setContentType(null, response);		
		model.addAttribute("error", hasError);
		model.addAttribute("returnUrl", returnUrl);				
		//login form for update page
        //if login error, get the targetUrl from session again.
		String targetUrl = getRememberMeTargetUrlFromSession(request);
		log.debug("targetUrl : {}", targetUrl);
		if(StringUtils.isNotEmpty(targetUrl)){
			model.addAttribute("targetUrl", targetUrl);
			model.addAttribute("loginUpdate", true);
		} 
		return getPageView(request, response,"login.html", "/accounts/login", model);
    }

	@RequestMapping(value={"/join","/signup", "/register"}, method = { RequestMethod.POST, RequestMethod.GET } )
    public Object displaySignupPage(@RequestParam(value="url", defaultValue="/", required=false ) String returnUrl,
    		HttpServletRequest request, 
    		HttpServletResponse response, 
    		Model model) {		
		ServletUtils.setContentType(null, response);		
		model.addAttribute("returnUrl", returnUrl);		 
		return getPageView(request, response, "join.html", "/accounts/register", model);
    }
	
	
	private String getRememberMeTargetUrlFromSession(HttpServletRequest request){
		String targetUrl = "";
		HttpSession session = request.getSession(false);
		if(session!=null){
			targetUrl = session.getAttribute("targetUrl")==null?"":session.getAttribute("targetUrl").toString();
		}
		return targetUrl;
	}
	
	
	private Object getPageView(HttpServletRequest request, HttpServletResponse response, String filename, String defaultView , Model model) {
		String view = defaultView ;
		int version = 1;
		if( isSetPageService() )
		try {
			Page page = pageService.getPage(filename, version );
			if( page != null ) {
				model.addAttribute("__page", page);				
			}

			if(StringUtils.isNotEmpty(page.getScript())) {
				log.debug("page script view : {}", page.getScript() );
				try {
					boolean useCache = page.getPageState() == PageState.PUBLISHED ? true : false ;
					View _view = communityGroovyService.getService(page.getScript(), View.class, useCache);
					_view.render((Map) model, request, response); 
				} catch (Exception e) { 
					log.error("Error in render.", e);
				}
			}
			
			if( page!= null && StringUtils.isNotEmpty( page.getTemplate() ) )
			{
				view = page.getTemplate(); 
				if(communitySpringEventPublisher!=null)
					communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
						.objectTypeAndObjectId(Models.PAGE.getObjectType(), page.getPageId())
						.action(AuditLogEvent.READ)
						.code(this.getClass().getName())
						.resource(page.getName()).build()); 
				
				if( StringUtils.isEmpty(view)) {
					return ServletUtils.doResponseAsHtml(page);
				}
				view = StringUtils.removeEnd(view, ".ftl");					
			} 
		} catch (PageNotFoundException e) {
			if(communitySpringEventPublisher!=null)
				communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, SecurityHelper.getAuthentication()))
					.objectTypeAndObjectId(Models.PAGE.getObjectType(), -1L)
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.resource(filename).build());
		}
		log.debug("VIEW: {}.", view );
		return view;
	}
	
}
