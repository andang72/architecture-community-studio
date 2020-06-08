package architecture.community.web.spring.controller.gateway;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.util.UrlPathHelper;

import architecture.community.web.spring.controller.annotation.ScriptData;

public class AbstractGroovyController {
	
	static final String USE_DEFAULT_SUFFIX_PATTERN = GroovyAPIsController.class.getName() + ".useDefaultSuffixPattern"; 
	
	private UrlPathHelper pathHelper = new UrlPathHelper();
		
	private AntPathMatcher pathMatcher = new AntPathMatcher(); 
	 
	private boolean useDefaultSuffixPattern = true;
	
	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	@Autowired(required=false)
	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
	
	private WebDataBinderFactory webDataBinderFactory ;
	
	protected PathMatcher getPathMatcher () {
		return pathMatcher;
	}
	
	protected UrlPathHelper getUrlPathHelper() {
		return pathHelper;
	}
	

	/**
	 * Expose the URI templates variables as request attribute.
	 * @param uriTemplateVariables the URI template variables
	 * @param request the request to expose the path to
	 * @see #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
	 */
	protected void exposeUriTemplateVariables(Map<String, String> uriTemplateVariables, HttpServletRequest request) {
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
	}
	
	protected void setUriTemplateVariables(String urlPath, String pattern, HttpServletRequest request) {
		Map<String, String> uriTemplateVariables = new LinkedHashMap<String, String>(); 
		if(!StringUtils.isEmpty(pattern))
			uriTemplateVariables.putAll( pathMatcher.extractUriTemplateVariables(pattern, urlPath) ); 
		if (log.isDebugEnabled()) {
			log.debug("URI Template variables for request [" + urlPath + "] are " + uriTemplateVariables);
		} 
		exposeUriTemplateVariables(uriTemplateVariables, request); 
	}
	
	protected String getRequestPath(HttpServletRequest request) {
		if (this.pathHelper != null) {
			return this.pathHelper.getPathWithinApplication(request);
		}
		String url = request.getServletPath(); 
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
		}
		return url;
	}
	
	protected Method resolveHandlerMethod(Class<?> handlerType) {
		final ArrayList<Method> methods = new ArrayList<Method>();
		ReflectionUtils.doWithMethods(
			handlerType, 
			new MethodCallback() { 
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					methods.add(method);
				} 
			},
			new MethodFilter() {
				@Override
				public boolean matches(Method method) {
					return method.isAnnotationPresent(ScriptData.class);
				}
			}
		);	
		return methods.isEmpty() ? null : methods.get(0);
	}
	
	protected List<Object> resolveHandlerMethodArguments(Method method, NativeWebRequest webRequest) throws Exception {
		int parameterCount = method.getParameterCount() ;  
		ArrayList<Object> args = new ArrayList<Object>(parameterCount);   
		for( int index = 0 ; index< parameterCount ;index++) {
			MethodParameter parameter = new MethodParameter (method, index); 
			Object value = null;
			for( HandlerMethodArgumentResolver resolver : requestMappingHandlerAdapter.getArgumentResolvers()) {
				if( resolver.supportsParameter(parameter)) {
					try {
						if( log.isDebugEnabled())
							log.debug("resolver({}) for {}", resolver.getClass().getName(), parameter);
						value = resolver.resolveArgument(parameter, null, webRequest, webDataBinderFactory);
						if( log.isDebugEnabled())
							log.debug("resolved value : {}", value ); 
					} catch (Exception e) {
						if( e instanceof org.springframework.http.converter.HttpMessageNotReadableException ) {
							throw e;
						} 
						
					}
				}
			} 
			if( value == null ) {

				if( Long.class.equals(parameter.getParameterType()) ) {
					value = 0L;
				}else if (Integer.class.equals(parameter.getParameterType())) 
				{
					value = 0;
				}else {
					value = null;
				}
			}
			args.add(value);
		}
		return args;
	}
	
	protected boolean isPattern(String pattern) {
		return pattern == null ? false : pathMatcher.isPattern(pattern);
	}
	
	protected WebDataBinderFactory getWebDataBinderFactory() {
		if( webDataBinderFactory == null) {
			ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
			initializer.setConversionService(new DefaultConversionService());
			webDataBinderFactory = new DefaultDataBinderFactory(initializer);
		}
		return webDataBinderFactory;
	} 
	
	/**
	 * Derive URL mappings from the handler's method-level mappings.
	 * @param handlerType the handler type to introspect
	 * @param hasTypeLevelMapping whether the method-level mappings are nested
	 * within a type-level mapping
	 * @return the array of mapped URLs
	 */
	protected String[] determineUrlsForHandlerMethods(Class<?> handlerType, final boolean hasTypeLevelMapping) {
		String[] subclassResult = determineUrlsForHandlerMethods(handlerType);
		if (subclassResult != null) {
			return subclassResult;
		}
		final Set<String> urls = new LinkedHashSet<String>();
		Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
		handlerTypes.add(handlerType);
		handlerTypes.addAll(Arrays.asList(handlerType.getInterfaces()));
		for (Class<?> currentHandlerType : handlerTypes) {
			ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) {
					RequestMapping mapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
					if (mapping != null) {
						String[] mappedPatterns = mapping.value();
						if (mappedPatterns.length > 0) {
							for (String mappedPattern : mappedPatterns) {
								if (!hasTypeLevelMapping && !mappedPattern.startsWith("/")) {
									mappedPattern = "/" + mappedPattern;
								}
								addUrlsForPath(urls, mappedPattern);
							}
						}
						else if (hasTypeLevelMapping) {
							// empty method-level RequestMapping
							urls.add(null);
						}
					}
				}
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}
		return StringUtils.toStringArray(urls);
	}
	
	/**
	 * Derive URL mappings from the handler's method-level mappings.
	 * @param handlerType the handler type to introspect
	 * @return the array of mapped URLs
	 */
	protected String[] determineUrlsForHandlerMethods(Class<?> handlerType) {
		return null;
	}
	
	protected void addUrlsForPath(Set<String> urls, String path) {
		urls.add(path);
		if (this.useDefaultSuffixPattern && path.indexOf('.') == -1 && !path.endsWith("/")) {
			urls.add(path + ".*");
			urls.add(path + "/");
		}
	}

}
