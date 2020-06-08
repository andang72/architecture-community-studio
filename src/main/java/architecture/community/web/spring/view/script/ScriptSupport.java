package architecture.community.web.spring.view.script;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.page.Page;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.user.User;
import architecture.community.util.CommunityContextHelper;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.util.ServletUtils;
import architecture.ee.exception.ComponentNotFoundException;
import architecture.ee.util.StringUtils;

public abstract class ScriptSupport {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected boolean isMultipartHttpServletRequest(HttpServletRequest request) {
		if (request instanceof MultipartHttpServletRequest ) {
			return true;
		}
		return false;
	}
	
	protected MultipartHttpServletRequest getMultipartHttpServletRequest(HttpServletRequest request) {
		return (MultipartHttpServletRequest) request;
	}
	
	protected User getUser() {
		return SecurityHelper.getUser();
	}
	
	protected boolean isUserInRole(String roles) {
		return SecurityHelper.isUserInRole(roles);
	}

	protected Page getPage(Map<String, ?> model){
		return (Page) model.get("__page");
	} 
	
	protected Map<String, ?> getVariables(Map<String, ?> model){
		return (Map<String, ?>) model.get("__variables");
	} 
	
	protected DataSourceRequest getDataSourceRequest(HttpServletRequest request) {
		return getRequestBodyObject(DataSourceRequest.class, request );
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getRequestBodyObject(Class<T> requiredType, HttpServletRequest request) {
		HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        try {
			return  (T) converter.read(requiredType, inputMessage );
		
        } catch (IOException e) {
			log.warn("error : {}", e);
			return null;
		}
	} 
	
	private String getObjectAsString( Object obj ) {
		return obj == null ? null : obj.toString();
	}
	
	public String getProperty( Map<String, ?> properties, String name, String defaultValue) {
		Object obj = properties.get( name ) ;
		return StringUtils.defaultString( getObjectAsString(obj), defaultValue );
	}
	
	public Boolean getBooleanProperty( Map<String, ?> properties , String key, Boolean defaultValue ) {
		String value = getObjectAsString(properties.get(key));
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) { } 
		return defaultValue;
	}
	
	public Integer getIntegerProperty( Map<String, ?> properties , String key, Integer defaultValue ) {
		String value = getObjectAsString(properties.get(key));
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {} 
		return defaultValue;
	}	
	
	public Long getLongProperty( Map<String, ?> properties , String key, Long defaultValue ) {
		String value = getObjectAsString(properties.get(key));
		try {
			return Long.parseLong(value);
		} catch (Exception e) { } 
		return defaultValue;
	}	
	
	protected String toDateAsJsonValue (Map<String, ?> row, String key, String defaultValue) {
		log.debug(" class {}={}", key, row.get(key) ) ;
		String json = null;
		Object obj = row.get(key);
		if( obj!= null && obj instanceof Date) {
			Date date = (Date) row.get(key);
			json =  ServletUtils.getDataAsISO8601(date);
		}
		if(obj!=null) {
			log.debug("class {}", obj.getClass().getName());
		}
		return StringUtils.defaultString(json, defaultValue);
	} 	
	
	protected <T> T getComponent(String requiredName, Class<T> requiredType) {
		return CommunityContextHelper.getComponent(requiredName, requiredType);
	}
	
	/**
	 * 인자로 전달된 데이터소스로 사용자정의 쿼리 서비스를 생성한다.
	 * 
	 * @param dataSource datasource bean name.
	 * @return
	 */
	protected CustomQueryJdbcDao createCustomQueryJdbcDao(String dataSource) {  
		try {
			DataSource dataSourceToUse = CommunityContextHelper.getComponent(dataSource, DataSource.class);
			CustomQueryJdbcDao customQueryJdbcDao = new CustomQueryJdbcDao();
			CommunityContextHelper.autowire(customQueryJdbcDao);
			customQueryJdbcDao.setDataSource(dataSourceToUse);
			return customQueryJdbcDao;
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", dataSource, DataSource.class.getName() ), e);
		} 
	} 
}
