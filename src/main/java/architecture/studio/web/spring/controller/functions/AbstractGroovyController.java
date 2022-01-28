package architecture.studio.web.spring.controller.functions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.util.UrlPathHelper;

import architecture.community.page.api.Api;
import architecture.community.web.spring.controller.annotation.ScriptData;
import architecture.community.web.util.ParamUtils;

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
	
	
	/**
	 * 인자로 전달된 함수 인자의 데이터 타입에 따라서 값 설정.
	 * 
	 */
	protected List<Object> resolveHandlerMethodArguments(Api api, Method method, NativeWebRequest webRequest) throws Exception {
		int parameterCount = method.getParameterCount() ;  
		
		ArrayList<Object> args = new ArrayList<Object>(parameterCount);  
		ModelAndViewContainer mavContainer = new ModelAndViewContainer();
		for( int index = 0 ; index< parameterCount ;index++) {
			MethodParameter parameter = new MethodParameter (method, index); 
			Object value = null;
			if( log.isDebugEnabled())
				log.debug("MethodParameter : {}", index ); 
			
			
			for( HandlerMethodArgumentResolver resolver : requestMappingHandlerAdapter.getArgumentResolvers()) {
				boolean supports = resolver.supportsParameter(parameter);
				if( log.isDebugEnabled())
					log.debug("<Method Argument Resolver class='{}' , supports='{}'>", resolver.getClass().getName(), supports );
				if( supports ) {
					try {
						//if( log.isDebugEnabled())
						//	log.debug("resolved ({}) for {}", resolver.getClass().getName(), parameter);
						value = resolver.resolveArgument(parameter, mavContainer, webRequest, getWebDataBinderFactory());
						//if( log.isDebugEnabled())
						//	log.debug("Method argument resolved {} -> {} by {}", parameter, value ); 
					} catch (Exception e) {
						if( e instanceof org.springframework.http.converter.HttpMessageNotReadableException ) {
							throw e;
						} else {
							//log.warn(e.getMessage(), e);
						}
					} 
				}
			} 

			if( value == null && Api.class.equals(parameter.getParameterType()) ) {
				value = api;
			}
			if (value == null && parameter.hasParameterAnnotation(RequestParam.class)) {
				RequestParam ann = parameter.getParameterAnnotation(RequestParam.class);
				String val = ParamUtils.getParameter(webRequest.getNativeRequest(HttpServletRequest.class), ann.name(), ann.defaultValue());
				parameter.getParameterType();
				if( String.class.equals(parameter.getParameterType()) ) {
					value = val;
				}else if( Long.class.equals(parameter.getParameterType()) ) {
					value = Long.parseLong(val);
				}else if (Integer.class.equals(parameter.getParameterType())) {
					value = Integer.parseInt(val);
				}else if (Boolean.class.equals(parameter.getParameterType())) {
					value = Boolean.parseBoolean(val);
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
	
	
	public static MethodParameter createMethodParameter(Parameter parameter) {
		Assert.notNull(parameter, "Parameter must not be null");
		Executable executable = parameter.getDeclaringExecutable();
		if (executable instanceof Method) {
			return new MethodParameter((Method) executable, getIndex(parameter));
		}
		// else
		return new MethodParameter((Constructor<?>) executable, getIndex(parameter));
	}

	private static int getIndex(Parameter parameter) {
		Assert.notNull(parameter, "Parameter must not be null");
		Executable executable = parameter.getDeclaringExecutable();
		Parameter[] parameters = executable.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] == parameter) {
				return i;
			}
		}
		throw new IllegalStateException(String.format("Failed to resolve index of parameter [%s] in executable [%s]", parameter, executable.toGenericString()));
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
