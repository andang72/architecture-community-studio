package ${data.packageName};

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.user.User
import architecture.community.exception.UnAuthorizedException;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.util.ParamUtils;
<#if data.superClassName == 'data' >
import architecture.community.web.spring.view.script.AbstractDataView;
<#elseif data.superClassName == 'page'>
import architecture.community.web.spring.view.script.AbstractPageView;
import architecture.community.page.Page; 
</#if>

// dependency 
<#list data.dependencies as dependency>
import ${dependency?trim};
</#list>

/**
* ${data.className}
*
* script type : ${data.superClassName}
* created : ${.now}
*/
public class ${data.className} <#if data.superClassName == 'data' >extends AbstractDataView <#elseif data.superClassName == 'page' >extends AbstractPageView </#if>
{
<#list data.services as item >
	@Autowired(required=${item.required?string})
	@Qualifier("${item.name}")
	private ${item.className} ${item.name};  
	
</#list>
<#if data.superClassName == 'data' >
	public Object handle(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug( "${data.className}" );
	<#if ( data.requiredRoles?size > 0 ) >
		// Checking user has required roles .
		if( !isUserInRole(<#list data.requiredRoles as item >"${item}"<#if item?is_last == false >,</#if></#list>) )
			throw new UnAuthorizedException();
	</#if>	 
	<#if data.setUser >
		// User 
		User user = getUser();
		
	</#if> 
	<#if data.setMultipart >
		// Multipart
		if( isMultipartHttpServletRequest (request){
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request ;
		}
	</#if>
	<#list data.parameters as item >
		// Request Parameters 
		String ${item.name} = ParamUtils.getParameter(request, "${item.name}",  "${item.defaultValue}" );
		
	</#list>
	<#if data.requestBody.enabled >
		// Request Body Binding
		<#if data.requestBody.className == "architecture.community.web.model.DataSourceRequest">
		${data.requestBody.className} ${data.requestBody.name} = getDataSourceRequest(request);
		<#else>
		${data.requestBody.className} ${data.requestBody.name} = getRequestBodyObject(${data.requestBody.className}.class, request);
		</#if>		
	</#if> 
	
	<#list data.services as item >		
	<#if item.className == "architecture.community.query.CustomQueryService" >
		// Example 1 . Transaction code with return.
		List list = ${item.name}.execute(new CustomTransactionCallback<List>() { 
			public List doInTransaction(architecture.community.query.dao.CustomQueryJdbcDao dao) {
				return dao.getJdbcTemplate().queryForList("SELECT * FROM AC_UI_SEQUENCER"); 
			}	
		});
		
		// Example 2 .Transaction code with no return.
		${item.name}.execute(new CustomTransactionCallbackWithoutResult() { 
			protected void doInTransactionWithoutResult(architecture.community.query.dao.CustomQueryJdbcDao dao) {
				// dodo.
			}	
		});
	</#if>
	</#list>
		return "welcome to island.";
	}
<#elseif data.superClassName == 'page' >	
	protected void renderMergedOutputModel( Map<String, ?> model,  HttpServletRequest request,  HttpServletResponse response) throws Exception { 
		
		log.debug( "${data.className}" );
		
	<#if ( data.requiredRoles?size > 0 ) >
		// Checking user has required roles .
		if( !isUserInRole(<#list data.requiredRoles as item >"${item}"<#if item?is_last == false >,</#if></#list>) )
			throw new UnAuthorizedException();
			
	</#if>	 
	<#if data.setUser >
		// User 
		User user = getUser();
		
	</#if>	 
	<#if data.setMultipart >
		// Multipart
		if( isMultipartHttpServletRequest (request){
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request ;
		}
	</#if>	
	<#list data.parameters as item >
		// Request Parameters 
		String ${item.name} = ParamUtils.getParameter(request, "${item.name}",  "${item.defaultValue}" );
		
	</#list>
	<#if data.requestBody.enabled >
		// Request Body Binding
		<#if data.requestBody.className == "architecture.community.web.model.DataSourceRequest">
		${data.requestBody.className} ${data.requestBody.name} = getDataSourceRequest(request);
		<#else>
		${data.requestBody.className} ${data.requestBody.name} = getRequestBodyObject(${data.requestBody.className}.class, request);
		</#if>
		// Page 
		Page page = getPage(model);
				
	</#if>	
	}	
</#if>
}