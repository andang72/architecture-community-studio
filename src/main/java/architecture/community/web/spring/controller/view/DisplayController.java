package architecture.community.web.spring.controller.view;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.util.UrlPathHelper;

import architecture.community.audit.event.AuditLogEvent;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.model.Models;
import architecture.community.page.BodyType;
import architecture.community.page.Page;
import architecture.community.page.PageMaker;
import architecture.community.page.PageService;
import architecture.community.page.PageState;
import architecture.community.page.PathPattern;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.security.spring.acls.PermissionsBundle;
import architecture.community.services.CommunityGroovyService;
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.util.SecurityHelper;
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;

@Controller("community-display-controller")
@RequestMapping("/display")
public class DisplayController implements ServletContextAware {

	private static final String BINDING = "binding.";
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required=false)
	@Qualifier("pageService")
	private PageService pageService; 
	
	@Autowired(required=false)
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required = false) 
	@Qualifier("aclService")
	private CommunityAclService aclService;	
	
	@Autowired(required=false)
	@Qualifier("communityGroovyService")
	private CommunityGroovyService communityGroovyService;

	@Autowired(required=false)
	@Qualifier("freemarkerConfig")
	private FreeMarkerConfig freeMarkerConfig ;
	
	@Autowired(required=false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	 
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	private ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	} 
	 
	
	@RequestMapping(value = {"/view/{filename:.+}", "/pages/{filename:.+}"}, method = { RequestMethod.POST, RequestMethod.GET })
	public Object page (
			@PathVariable String filename,
			@RequestParam(value = "version", defaultValue = "1", required = false) int version,
			@RequestParam(value = "preview", defaultValue = "false", required = false) boolean preview,
		HttpServletRequest request, 
		HttpServletResponse response, 
		Model model) 
		throws NotFoundException, UnAuthorizedException {	
		
		log.debug("loading page : {} ({})", filename, version );
		Page page = pageService.getPage(filename, version);	
		model.addAttribute("__page", page);  
		
		if( page.getPageId() > 0 ) {
			if( page.isSecured() ) {
				PermissionsBundle bundle = aclService.getPermissionBundle(SecurityHelper.getAuthentication(), Models.PAGE.getObjectClass(), page.getPageId());
				if( !bundle.isRead() )
					throw new UnAuthorizedException();
			} 
			if( viewCountService != null && !preview  )
				viewCountService.addViewCount(page);	
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
		
		if( StringUtils.isNotEmpty( page.getBodyText()) && page.getBodyContent().getBodyType() == BodyType.FREEMARKER ) {
			try {
				log.debug("page body process as {}.", page.getBodyContent().getBodyType() );
				PageMaker.Builder builder = PageMaker.newBuilder().configuration(freeMarkerConfig.getConfiguration()).servletContext(servletContext).page(page).model(model).request(request);			
				builder.buildPageBody();				
			} catch (Exception e) {
				log.error("Error in parsing template.", e);
			}
		} 
		
		if(communitySpringEventPublisher!=null)
			communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
				.objectTypeAndObjectId(Models.PAGE.getObjectType(), page.getPageId())
				.action(AuditLogEvent.READ)
				.code(this.getClass().getName())
				.resource(page.getName()).build()); 
		
		String view = page.getTemplate(); 
		
		if( StringUtils.isEmpty(view)) {
			return ServletUtils.doResponseAsHtml(page);
		} 
		
		Assert.notNull(view, "view cannot be null"); 
		log.debug("final view is '{}'", view );
		
		if(StringUtils.endsWith(view, ".ftl")) {
			ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
			view = StringUtils.removeEnd(view, ".ftl");	
		}else if (StringUtils.endsWith(view, ".jsp")) {
			view = StringUtils.removeEnd(view, ".jsp");	
		}
		
		return view;
	}
	 
	
	private static final AntPathMatcher pathMatcher = new AntPathMatcher(); 
	private static final UrlPathHelper pathHelper = new UrlPathHelper();
	
	/**
	 * 
	 * pattern 기반의 페이지 호출 처리 .
	 * 
	 */
	@RequestMapping(value = {"/view/*/**", "/pages/*/**"}, method = { RequestMethod.POST, RequestMethod.GET })
    public Object pattern (
    	@RequestParam(value = "version", defaultValue = "1", required = false) int version,
    	@RequestParam(value = "preview", defaultValue = "false", required = false) boolean preview,
    	HttpServletRequest request, 
	    HttpServletResponse response, 
	    Model model) 
	    throws NotFoundException, UnAuthorizedException {	 
		 
		
		
		String path = pathHelper.getLookupPathForRequest(request);
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		log.debug("checking for {}" , path );
		for( PathPattern pattern : pageService.getPathPatterns("**") ) {
			log.debug("mapping {} = {}", pattern.getPattern(), mapping.match(request, pattern.getPattern()) );
			
		}
		//mapping.match(request, pattern)
		
		
		
 		
 		Page pageToUse = null;
 		for( PathPattern pattern : pageService.getPathPatterns("/display/pages") )
 		{ 	
 			boolean isPattern = pathMatcher.isPattern(pattern.getPattern()); 
 			boolean match = false;
 			Map<String, String> variables = null; 
 			if( isPattern) {
 				AntPathRequestMatcher matcher = new AntPathRequestMatcher(pattern.getPattern());
 				match = matcher.matches(request) ;
 				variables = matcher.extractUriTemplateVariables(request);  
 			}else {  
 				variables = pathMatcher.extractUriTemplateVariables(pattern.getPattern(), path); 
 				match = pathMatcher.match( pattern.getPattern(), path);
 			}	
 			
 			log.debug("Path({}) Pattern Checking (pattern:{}) : {}, match : {}, variables: {}", path, isPattern, pattern.getPattern(), match, variables);
 			
 			if( match ) {  
 				Page page = pageService.getPage(pattern.getObjectId(), version);
 				if( page.getPageId() > 0 ) {
 					if( page.isSecured() ) {
 						PermissionsBundle bundle = aclService.getPermissionBundle(SecurityHelper.getAuthentication(), Models.PAGE.getObjectClass(), page.getPageId());
 						if( !bundle.isRead() )
 							throw new UnAuthorizedException();
 					}
 					if( viewCountService!=null && !preview  )
 						viewCountService.addViewCount(page);	
 				}
 				model.addAttribute("__page", page); 
 				model.addAttribute("__variables", variables);  
 				
 				pageToUse = page ;
 				
 				if(StringUtils.isNotEmpty(page.getScript())) {
 					boolean useCache = page.getPageState() == PageState.PUBLISHED ? true : false ;
 					View _view = communityGroovyService.getService(page.getScript(), View.class, useCache );
 					try {
						_view.render((Map) model, request, response);
					} catch (Exception e) { 
						e.printStackTrace();
					}
 				}
 				
 				if(communitySpringEventPublisher!=null)
 					communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
 							.objectTypeAndObjectId(Models.PAGE.getObjectType(), page.getPageId())
 							.action(AuditLogEvent.READ)
 							.code(this.getClass().getName())
 							.resource(page.getName()).build()); 		
 				break;
 			}
 		}
 		
 		if( pageToUse == null)
 		{
 			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
 		}
 		
 		String view = pageToUse.getTemplate(); 
 		if( StringUtils.isEmpty(view)) {
			return ServletUtils.doResponseAsHtml(pageToUse);
		} 
		
		if(StringUtils.endsWith(view, ".ftl")) {
			ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
			view = StringUtils.removeEnd(view, ".ftl");	
		}else if (StringUtils.endsWith(view, ".jsp")) {
			view = StringUtils.removeEnd(view, ".jsp");	
		}
		return view;
	}

}
