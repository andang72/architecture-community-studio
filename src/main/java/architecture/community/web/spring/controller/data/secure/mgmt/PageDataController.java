package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.model.Property;
import architecture.community.page.DefaultBodyContent;
import architecture.community.page.DefaultPage;
import architecture.community.page.Page;
import architecture.community.page.PageService;
import architecture.community.page.PageView;
import architecture.community.query.CustomQueryService;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.Utils;
import architecture.ee.service.ConfigService;

@Controller("community-mgmt-page-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class PageDataController {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired( required = false) 
	@Qualifier("pageService")
	private PageService pageService;
	
	@Autowired( required = false) 
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired( required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("aclService")
	private CommunityAclService communityAclService;
	
	public PageDataController() { 
	}
 

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getPages(
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "skip", defaultValue = "0", required = false) Integer skip,
			@RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize,
			@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
			NativeWebRequest request) {

		boolean includeBodyContent = StringUtils.containsOnly(fields, "bodyContent"); 
		int objectType = -1;
		long objectId = -1L; 
		ItemList items = new ItemList();
		
		int total = pageService.getPageCount(objectType, objectId);
		if (total > 0) {
			List<Page> list ; 
			if( pageSize > 0 )
				list = pageService.getPages(objectType, objectId, page, pageSize);
			else
				list = pageService.getPages(objectType, objectId); 
			
			List<Page> list2 = new ArrayList<Page>(list.size());
			
			for (Page p : list) {
				list2.add(PageView.build(p, includeBodyContent));
			}
			
			log.debug( "page view : {} > {}", list, list2 );
			items.setTotalCount(total);
			items.setItems(list2);
		} 
		log.debug("pageSize : {} , total {} ",  pageSize, items.getTotalCount());
		
		return items; 
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
	public Result saveOrUpdate(
			@RequestBody DefaultPage page,
			@RequestParam(value = "fields", defaultValue = "", required = false) String fields,
			NativeWebRequest request) throws NotFoundException {

		boolean includeBodyContent = StringUtils.containsOnly(fields, "bodyContent");
		boolean includeProperties = StringUtils.containsOnly(fields, "properties");

		boolean doUpdate = false;
		User user = SecurityHelper.getUser();

		Page pageToUse;
		if (page.getPageId() > 0) {
			pageToUse = pageService.getPage(page.getPageId());
			if (!StringUtils.equals(page.getName(), pageToUse.getName())
					|| !StringUtils.equals(page.getTitle(), pageToUse.getTitle())
					|| pageToUse.getPageState() != page.getPageState()
					|| !StringUtils.equals(page.getSummary(), pageToUse.getSummary()) 
					|| !StringUtils.equals(page.getTemplate(), pageToUse.getTemplate()) 
					|| pageToUse.isSecured() != page.isSecured()
					|| !StringUtils.equals(page.getPattern(), pageToUse.getPattern()) 
					|| !StringUtils.equals(page.getScript(), pageToUse.getScript()) 
					|| includeBodyContent 
					|| includeProperties ) {
				doUpdate = true;
			}
		} else {
			pageToUse = new DefaultPage(page.getObjectType(), page.getObjectId());
			pageToUse.setUser(user);
			pageToUse.setBodyContent(new DefaultBodyContent());
			doUpdate = true;
		}

		if (doUpdate) { 
			pageToUse.setName(page.getName());
			pageToUse.setTitle(page.getTitle()); 
			if( page.getPageState() != null)
				pageToUse.setPageState(page.getPageState());
			
			pageToUse.setPattern(page.getPattern());
			pageToUse.setScript(page.getScript());
			pageToUse.setSummary(page.getSummary());
			pageToUse.setTemplate(page.getTemplate());
			pageToUse.setSecured(page.isSecured()); 
			
			if (includeBodyContent && page.getBodyContent() != null)
				pageToUse.setBodyText(page.getBodyContent().getBodyText());		 
			
			if (includeProperties)
				pageToUse.setProperties(page.getProperties());  
			
			pageService.saveOrUpdatePage(pageToUse);
		}
		return  Result.newResult("item", pageToUse );
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/update-state.json", method = RequestMethod.POST)
	@ResponseBody
	public Result updatePageState(@RequestBody DefaultPage page, NativeWebRequest request) throws NotFoundException { 
		
		Page target = pageService.getPage(page.getPageId());
		target.setPageState(page.getPageState());
		pageService.saveOrUpdatePage(target); 
		
		return Result.newResult();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/{pageId:[\\p{Digit}]+}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Page getPage(@PathVariable Long pageId,
			@RequestParam(value = "versionId", defaultValue = "1") Integer versionId, 
			@RequestParam(value = "fields", defaultValue = "", required = false) String fields,
			NativeWebRequest request)
			throws NotFoundException {
		boolean includeBodyContent = StringUtils.containsOnly(fields, "bodyContent");		
		Page page = pageService.getPage(pageId, versionId);
		return PageView.build(page, includeBodyContent);
	}	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/{pageId:[\\p{Digit}]+}/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result deletePage(@PathVariable Long pageId,
			@RequestParam(value = "versionId", defaultValue = "1") Integer versionId,  
			NativeWebRequest request)
			throws NotFoundException {
		
		Result result = Result.newResult(); 
		
		Page page = pageService.getPage(pageId, versionId);
		log.debug("delete page by id : {}", page.getPageId());
		pageService.deletePage(page); 
		
		ObjectIdentity identity = new ObjectIdentityImpl(Models.PAGE.getObjectClass(), page.getPageId()); 
		communityAclService.deleteAcl(identity, true);
		
		return result;
	
	}		
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/{pageId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getPageProperties(@PathVariable Long pageId,
			@RequestParam(value = "versionId", defaultValue = "1") Integer versionId, NativeWebRequest request)
			throws NotFoundException { 
		if (pageId <= 0) {
			return Collections.EMPTY_LIST;
		}
		
		Page page = pageService.getPage(pageId, versionId);
		Map<String, String> properties = page.getProperties(); 
		return Utils.toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/{pageId:[\\p{Digit}]+}/properties/update.json", method = RequestMethod.POST)
	@ResponseBody
	public List<Property> updatePageProperties(
			@PathVariable Long pageId,
			@RequestParam(value = "versionId", defaultValue = "1") Integer versionId,
			@RequestBody List<Property> newProperties, 
			NativeWebRequest request) throws NotFoundException {
		
		 
		Page page = pageService.getPage(pageId, versionId);
		Map<String, String> properties = page.getProperties();
		
		// update or create
		for (Property property : newProperties) {
			properties.put(property.getName(), property.getValue().toString());
		}
		
		if (newProperties.size() > 0) {
			pageService.saveOrUpdatePage(page);
		}
		
		return Utils.toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/pages/{pageId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.DELETE })
	@ResponseBody
	public List<Property> deletePageProperties(
			@PathVariable Long pageId,
			@RequestParam(value = "versionId", defaultValue = "1") Integer versionId,
			@RequestBody List<Property> newProperties, 
			NativeWebRequest request) throws NotFoundException {
		 
		Page page = pageService.getPage(pageId, versionId);
		Map<String, String> properties = page.getProperties();
		for (Property property : newProperties) {
			properties.remove(property.getName());
		}
		if (newProperties.size() > 0) {
			pageService.saveOrUpdatePage(page);
		}
		
		return Utils.toList(properties);
	}
 
}
