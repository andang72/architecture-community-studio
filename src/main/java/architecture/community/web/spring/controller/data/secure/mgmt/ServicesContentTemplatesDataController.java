package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Property;
import architecture.community.query.CustomQueryService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.util.StringUtils;
import architecture.studio.components.templates.DefaultTemplates;
import architecture.studio.components.templates.Templates;
import architecture.studio.services.TemplatesService;

@Controller("community-mgmt-services-content-templates-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/content")
public class ServicesContentTemplatesDataController {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Autowired(required=false)
	@Qualifier("templatesService")
	private TemplatesService templatesService; 

	/**
	 * 
	 * CONTENT FORMS API
	 * OPJECT TYPE : 33
	 *  
	******************************************/
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/templates", "/templates/list.json"}, method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ItemList getContentTemplates(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {  
		dataSourceRequest.setStatement("STUDIO_CONTENT.COUNT_TEMPLATES_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("STUDIO_CONTENT.SELECT_TEMPLATES_IDS_BY_REQUEST"); 
		List<Long> IDs = customQueryService.list(dataSourceRequest, Long.class); 
		List<Templates> items = new ArrayList<Templates>(totalCount);
		for( Long formsId : IDs ) {
			try {
				Templates forms = templatesService.getTemplates(formsId);
				items.add(forms); 
			} catch (NotFoundException e) {
			}
		} 
		return new ItemList(items, totalCount ); 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/templates/{templatesId:[\\p{Digit}]+}"}, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Templates getContentTemplatesById(@PathVariable Long templatesId, NativeWebRequest request) throws NotFoundException {  
		return templatesService.getTemplates(templatesId);
	} 

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/templates/{templatesId:[\\p{Digit}]+}"}, method = { RequestMethod.DELETE }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result deleteTemplates(@PathVariable Long templatesId, NativeWebRequest request) throws NotFoundException { 
		Result result = Result.newResult();
		Templates forms = templatesService.getTemplates(templatesId);
		try {
			templatesService.remove(forms);
		} catch (Exception e) {
			result.setError(e);
		} 
		return result;
	} 	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/templates/{templatesId:[\\p{Digit}]+}"}, method = { RequestMethod.POST, RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Templates saveOrUpdateContentTemplates(@RequestBody DefaultTemplates templates, @PathVariable Long templatesId,  NativeWebRequest request) throws NotFoundException {  
		User me = SecurityHelper.getUser();
		DefaultTemplates toUse = templates;
		if(toUse.getTemplatesId() > 0  ) { 
			
			toUse = (DefaultTemplates)templatesService.getTemplates(templates.getTemplatesId()); 
			toUse.setObjectType(templates.getObjectType());
			toUse.setObjectId(templates.getObjectId());
			toUse.setDescription(templates.getDescription());
			
			if( !StringUtils.isNullOrEmpty(templates.getName()) && !StringUtils.equals(toUse.getName(), templates.getName()) )
			{
				toUse.setName(templates.getName());
			}
			if( !StringUtils.isNullOrEmpty(templates.getDisplayName()) && !StringUtils.equals(toUse.getDisplayName(), templates.getDisplayName()))
			{
				toUse.setDisplayName(templates.getDisplayName());
			}
			if( !StringUtils.isNullOrEmpty(templates.getSubject()) && !StringUtils.equals(toUse.getSubject(), templates.getSubject()) )
			{
				toUse.setSubject(templates.getSubject());
			} 
			if( !StringUtils.isNullOrEmpty(templates.getBody()) && !StringUtils.equals(toUse.getBody(), templates.getBody()) )
			{
				toUse.setBody(templates.getBody());
			} 
		} 
		toUse.setCreator(me);
		toUse.setModifier(me);  
		templatesService.saveOrUpdate(toUse);
		return toUse;
	} 

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/templates/{templatesId:[\\p{Digit}]+}/render"}, method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public RenderedTemplates getRenderedTemplates(@RequestBody List<Property> parameters, @PathVariable Long templatesId, @RequestParam(value = "fields", defaultValue = "none", required = false) String fields, NativeWebRequest request) throws NotFoundException {  
		Map<String, Object> model = new HashMap<String, Object>();
		RenderedTemplates t = new RenderedTemplates();
		if(templatesId > 0  ) {  
			for( Property p : parameters ) {
				model.put(p.getName(), p.getValue());
			}
			boolean includeSubject = org.apache.commons.lang3.StringUtils.contains(fields, "subject");   
			User me = SecurityHelper.getUser();
			model.put("me", me);
			Templates toUse = templatesService.getTemplates(templatesId); 
			t.name = toUse.getName();
			t.subject = toUse.getSubject();
			if( includeSubject ) {
				try {
					t.subject = templatesService.processSubject(toUse, model);
				} catch (Exception e) { 
					t.subject = e.getMessage();
				}
			} 
			try {
				t.body = templatesService.processBody(toUse, model);
				t.success = true;
			} catch (Exception e) { 
				t.body = e.getMessage();
			}
		}
		return t;
	} 	
	
	
	public static class RenderedTemplates {
		
		private boolean success = false ; 
		private String name; 
		private String subject; 
		private String body;
 
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		} 
	}
}
