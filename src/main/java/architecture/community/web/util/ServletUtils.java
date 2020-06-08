package architecture.community.web.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.RequestContext;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import architecture.community.page.Page;
import architecture.ee.util.StringUtils;

public class ServletUtils {

	private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);
	
	/** 디폴트로 웹 페이지 컨텐츠 타입. */
	public static final String DEFAULT_HTML_CONTENT_TYPE = "text/html;charset=UTF-8";
	
	public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
	
	public static final String DEFAULT_HTML_ENCODING = "UTF-8";
	
	private static final ISO8601DateFormat formatter = new ISO8601DateFormat();

	public static ResponseEntity<String> doResponseAsHtml(Page page){
		log.debug("reseponse as html.");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(org.springframework.http.MediaType.TEXT_HTML);
		return new ResponseEntity( page.getBodyHtml(), httpHeaders, HttpStatus.OK);
	}
	
	public static Long getStringAsLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) { }
		
		return 0L;
	}
	
	/**
	 * 한글 처리를 위하여 response의 Content Type 속성을 변경하는 유틸리티. 
	 * 
	 * <ul>
	 * 		<li>Freemarker 사용시 한글 처리를 위한 사용.<li>
	 * </ul>
	 * 
	 * @param contentType 컨텐츠 타입 값
	 * @param response 
	 */
	public static void setContentType(String contentType, HttpServletResponse response) {		
    	String contentTypeToUse = StringUtils.defaultString(contentType, DEFAULT_HTML_CONTENT_TYPE);
    	response.setContentType(contentTypeToUse);
    }
	
	public static String getRestOfTheUrl (HttpServletRequest request, HttpServletResponse response) { 
		String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		return restOfTheUrl;
	}
	/**
	 * 이전 요청했던 URL을 알아낸다.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getReturnUrl(HttpServletRequest request, HttpServletResponse response) { 
		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest == null) {
			return request.getSession().getServletContext().getContextPath();
		} 
		return savedRequest.getRedirectUrl();
	}
	
	
	public static String getResourceAsString(String location, String encoding, RequestContext context) { 
		try {
			Resource resource = context.getWebApplicationContext().getResource(location);
			if( resource.exists() ) {
				return FileUtils.readFileToString(resource.getFile(), StringUtils.defaultString(encoding, "UTF-8"));
			}
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAcceptJson(HttpServletRequest request){
		String accept = request.getHeader("accept");		
		if(StringUtils.countOccurrencesOf(accept, "json") > 0 )
			return true;
		else 
			return false;
	}
	
	public static String getEncodedFileName(String name) {
		try {
			return URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}
	public static String getDecodedFileName(String name) {
		try {
			return java.net.URLDecoder.decode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}
	
	public static String getDataAsISO8601(Date date) {
		return formatter.format(date);
	}

	
	public static Date getDateAsISO8601(String date){ 
		try {
			if ( StringUtils.isNullOrEmpty(date) )
				return null; 
			Date sDate = formatter.parse(date);
			return sDate;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String getDeviceType(HttpServletRequest request) {
		Device device = DeviceUtils.getCurrentDevice(request);        
	    if (device == null) {
	        return "device is null";
	    }
	    String deviceType = "unknown";
	    if (device.isNormal()) {
	        deviceType = "nomal";
	    } else if (device.isMobile()) {
	        deviceType = "mobile";
	    } else if (device.isTablet()) {
	        deviceType = "tablet";
	    }
	    return deviceType;
	}
	public static final String CONTEXT_ROOT_PATH = "";
	
    public static String getContextPath(HttpServletRequest request){    	
    	
    	if( StringUtils.isEmpty(request.getContextPath())){
    		return CONTEXT_ROOT_PATH ;
    	}else if ( org.apache.commons.lang3.StringUtils.equals( "/",  request.getContextPath().trim() ) ){
    		return CONTEXT_ROOT_PATH ;    		
    	}else{
    		return request.getContextPath();
    	}
    }
    
    public static String getServletPath(HttpServletRequest request) {
		String thisPath = request.getServletPath();
		if (thisPath == null) {
			String requestURI = request.getRequestURI();
			if (request.getPathInfo() != null)
				thisPath = requestURI.substring(0,
						requestURI.indexOf(request.getPathInfo()));
			else
				thisPath = requestURI;
		} else if (thisPath.equals("") && request.getPathInfo() != null)
			thisPath = request.getPathInfo();
		return thisPath;
	} 
    
    
    public static String getStackTrace( Exception e ) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();
	}
    
   

}
