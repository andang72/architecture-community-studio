package architecture.community.web.spring.controller.gateway;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.audit.event.AuditLogEvent;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.model.Models;
import architecture.community.page.PathPattern;
import architecture.community.page.api.Api;
import architecture.community.page.api.ApiService;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.security.spring.acls.PermissionsBundle;
import architecture.community.services.CommunityGroovyService;
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.Result;
import architecture.community.web.util.ServletUtils;

/**
 * This restful controller for groovy data api.
 * 
 * 
 * 
 * @author donghyuck.son
 *
 */
@RestController("groovy-apis-v1-data-controller")
@RequestMapping({"/data/v1", "/data/apis"})
public class GroovyAPIsController  extends AbstractGroovyController {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required = false)
	@Qualifier("communityGroovyService")
	private CommunityGroovyService communityGroovyService;

	@Autowired(required=false)
	@Qualifier("apiService")
	private ApiService apiService;
	
	@Autowired(required=false)
	@Qualifier("communityAclService")
	private CommunityAclService communityAclService;
	
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	public GroovyAPIsController() {
	}
	
	@RequestMapping(value = "/*/**", method = { RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH })
	public Object callByPatterns(
		@RequestParam(value = "version", defaultValue = "1", required = false) int version,
		@RequestParam(value = "preview", defaultValue = "false", required = false) boolean preview,	
		Model model,
		NativeWebRequest webRequest) throws Exception {
		
		Result result = Result.newResult(); 
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class); 
		StopWatch watch = new StopWatch();
		watch.start(); 
 		String path = getUrlPathHelper().getLookupPathForRequest(request); 
 		
 		PathPattern matchePattern = null;
 		for(PathPattern pathPattern:apiService.getPathPatterns("/data/v1")){
 			boolean isPattern = pathPattern.getPattern() == null ? false : getPathMatcher().isPattern(pathPattern.getPattern());
 			boolean match = getPathMatcher().match( pathPattern.getPattern(), path);
 			log.debug("{} checking (pattern:{}) match : {}", pathPattern.getPattern(), isPattern, match);
 			if( match ) {
 				matchePattern = pathPattern;
 				break;
 			}
 		}
 		for(PathPattern pathPattern:apiService.getPathPatterns("/data/apis")){
 			boolean isPattern = pathPattern.getPattern() == null ? false : getPathMatcher().isPattern(pathPattern.getPattern());
 			boolean match = getPathMatcher().match( pathPattern.getPattern(), path);
 			log.debug("{} checking (pattern:{}) match : {}", pathPattern.getPattern(), isPattern, match);
 			if( match ) {
 				matchePattern = pathPattern;
 				break;
 			}
 		}
 		
 		if(matchePattern == null)
 			throw new NotFoundException();
 		
 		Api api = apiService.getApiById(matchePattern.getObjectId()); 
 		
		if(!isAllowed(api))
			throw new UnAuthorizedException("Access Permission Required."); 
		
		ScriptSource scriptSource = communityGroovyService.getScriptSource(api.getScriptSource());
		GroovyScriptFactory factory = communityGroovyService.getGroovyScriptFactory(api.getScriptSource(), true);
		setUriTemplateVariables(path, matchePattern.getPattern(), request);  
		try {  
			Class<?> handlerType = communityGroovyService.getScriptedObjectType(scriptSource, factory);
			Object object = communityGroovyService.getScriptedObject(scriptSource, handlerType, factory, true);
			Method method = resolveHandlerMethod(handlerType); 
			List<Object> args = resolveHandlerMethodArguments(api, method, webRequest);
			String[] urls = determineUrlsForHandlerMethods(object.getClass(), false);
			for( String url : urls )
				log.debug("mapping url : {}", url);
			
			log.debug("{} called with {}", method.getName(), args);
			if( method.getReturnType().equals(Void.TYPE)){
				method.invoke(object, args.toArray()); 
			}else {
				return method.invoke(object, args.toArray()); 
			}
		}finally {
			watch.stop(); 
			log.debug("DATA API CALLED : {}" , watch.prettyPrint());
			if(communitySpringEventPublisher!=null && api!=null)
				communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
					.objectTypeAndObjectId(Models.API.getObjectType(), api.getApiId())
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.totalTimeSeconds(watch.getTotalTimeSeconds())
					.resource(api.getName()).build()); 
		}
		return result;
	}

	 
	@RequestMapping(value = "/{filename:.+}", method = { RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH }, produces = MediaType.APPLICATION_JSON_VALUE )
	public Object callByFilename(
			@PathVariable String filename,
			@RequestParam(value = "version", defaultValue = "1", required = false) int version,
			@RequestParam(value = "preview", defaultValue = "false", required = false) boolean preview, 
			Model model, 
			NativeWebRequest webRequest) throws Exception {
 
		 
		StopWatch watch = new StopWatch();
		watch.start();
	
		Result result = Result.newResult(); 
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		
		Api api = apiService.getApi(filename); 
		log.debug("DATA API : {}, verison:{}, priveiw:{}", filename, version, preview );
		
		if(!isAllowed(api))
			throw new UnAuthorizedException("Access Permission Required.");

		
		ScriptSource scriptSource = communityGroovyService.getScriptSource(api.getScriptSource());
		GroovyScriptFactory factory = communityGroovyService.getGroovyScriptFactory(api.getScriptSource(), true);
		
		if( StringUtils.isNotEmpty( api.getContentType() )){
			ServletUtils.setContentType(api.getContentType(), response);
		}
		
		String path =  getRequestPath(request);
		boolean isPattern = isPattern(api.getPattern()); 
		setUriTemplateVariables(path, api.getPattern(), request);
		 
		try {  
			Class<?> handlerType = communityGroovyService.getScriptedObjectType(scriptSource, factory);
			Object object = communityGroovyService.getScriptedObject(scriptSource, handlerType, factory, true);
			Method method = resolveHandlerMethod(handlerType); 
			List<Object> args = resolveHandlerMethodArguments(api, method, webRequest);
			String[] urls = determineUrlsForHandlerMethods(object.getClass(), false);
			
			for( String url : urls )
				log.debug("mapping url : {}", url);
			
			if( method.getReturnType().equals(Void.TYPE)){
				method.invoke(object, args.toArray()); 
			}else {
				return method.invoke(object, args.toArray()); 
			}
		}finally {
			watch.stop();
			log.debug("DATA API CALLED : {}" , watch.prettyPrint());
			if(communitySpringEventPublisher!=null && api!=null)
				communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
					.objectTypeAndObjectId(Models.API.getObjectType(), api.getApiId())
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.totalTimeSeconds(watch.getTotalTimeSeconds())
					.resource(api.getName()).build()); 
		}
		return result;
	}	
	
	
	private boolean isAllowed(Api api) {
		if(!api.isSecured())
			return true;
		PermissionsBundle bundle = communityAclService.getPermissionBundle(SecurityHelper.getAuthentication(), Models.API.getObjectClass(), api.getApiId());
		return bundle.isRead();
	}

}
