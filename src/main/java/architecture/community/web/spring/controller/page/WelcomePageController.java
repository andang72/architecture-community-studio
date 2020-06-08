package architecture.community.web.spring.controller.page;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;

@Controller("welcome-page-controller")
public class WelcomePageController {

	private static final Logger log = LoggerFactory.getLogger(WelcomePageController.class);	
	
	public static final String DEFAULT_WELCOME_PAGE = "index.html";
	public static final Integer DEFAULT_WELCOME_PAGE_VERSION = 1;
	
	@Autowired(required=false)
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required=false)
	@Qualifier("pageService")
	private PageService pageService;
	
	@Autowired(required=false)
	@Qualifier("communityGroovyService")
	private CommunityGroovyService communityGroovyService;	
	
	@Autowired(required=false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	private boolean isSetCommunityGroovyService() {
		return communityGroovyService != null;
	}
	
	private boolean isSetPageService() {
		return pageService != null;
	}
	
	public WelcomePageController() { 
	}

	@RequestMapping(value={"/", "/index", "/index.html"} , method = { RequestMethod.POST, RequestMethod.GET } )
    public String displayWelcomePage (HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String view = "index";
		
		if(isSetPageService())
		try { 
			Page page = pageService.getPage(DEFAULT_WELCOME_PAGE, DEFAULT_WELCOME_PAGE_VERSION ); 
			model.addAttribute("__page", page); 
			log.debug("page template is {}", view );  
			if( StringUtils.isNotEmpty(page.getScript()) && isSetCommunityGroovyService()) {
				boolean useCache = page.getPageState() == PageState.PUBLISHED ? true : false ;
				View _view = communityGroovyService.getService(page.getScript(), View.class, useCache);
				try {
					_view.render( model.asMap(), request, response);
					
				} catch (Exception e) {  
					log.warn(e.getMessage(), e);
				}
			}
			
			if( StringUtils.isNotEmpty( page.getTemplate() ) )
			{
				view = page.getTemplate(); 
				if(StringUtils.endsWith(view, ".ftl")) {
					ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
					view = StringUtils.removeEnd(view, ".ftl");	
				}else if (StringUtils.endsWith(view, ".jsp")) {
					view = StringUtils.removeEnd(view, ".jsp");
				}
			}
			
			if( viewCountService != null )
				viewCountService.addViewCount(page);	
			
			if(communitySpringEventPublisher!=null)
				communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
					.objectTypeAndObjectId(Models.PAGE.getObjectType(), page.getPageId())
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.resource(page.getName()).build());
			
		} catch (PageNotFoundException e) {
			ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
			communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, SecurityHelper.getAuthentication()))
					.objectTypeAndObjectId(Models.PAGE.getObjectType(), -1L)
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.resource("index.html").build());
		}
		isSetupRequired(request, response);
		return view;
    }
	
	public boolean isSetupRequired(HttpServletRequest request, HttpServletResponse response) { 
		//if( configService.isSetupComplete() 
		InetAddress.getLoopbackAddress();
		log.debug("LocalAddr {}, RemoteAddr{}, RemoteHost:{}", 
			request.getLocalAddr(), 
			request.getRemoteAddr(), 
			request.getRemoteHost()
		);
		return true;
	}
}
