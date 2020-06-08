package architecture.community.web.spring.controller.data.secure.mgmt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import architecture.community.navigator.menu.Menu;
import architecture.community.navigator.menu.MenuAlreadyExistsException;
import architecture.community.navigator.menu.MenuItem;
import architecture.community.navigator.menu.MenuItemNotFoundException;
import architecture.community.navigator.menu.MenuNotFoundException;
import architecture.community.navigator.menu.MenuService;
import architecture.community.query.CustomQueryService;
import architecture.community.query.ParameterValue;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.Utils;

@Controller("community-mgmt-resources-menu-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesMenuDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	@Qualifier("menuService")
	private MenuService menuService;
	
	@Autowired( required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("aclService")
	private CommunityAclService communityAclService;
	
	/**
	 * MENU API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList getAllMenus (
		@RequestBody DataSourceRequest dataSourceRequest,
		NativeWebRequest request) throws NotFoundException {		
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_MENU_IDS_BY_REQUEST");
		List<Long> menuIds = customQueryService.list(dataSourceRequest, Long.class);
		List<Menu> menus = new ArrayList<Menu>(menuIds.size());
		for( Long menuId : menuIds ) {
			menus.add(menuService.getMenuById(menuId));
		}		
		return new ItemList(menus, menus.size());	
	}
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/create.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result createMenu(@RequestBody Menu newMenu, NativeWebRequest request) throws MenuNotFoundException, MenuAlreadyExistsException { 
		menuService.createMenu(newMenu.getName(), newMenu.getDescription());
		return Result.newResult();
    }

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Menu getMenu(@PathVariable Long menuId, NativeWebRequest request) throws MenuNotFoundException { 
		return menuService.getMenuById(menuId);
    }
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result saveOrUpdateMenu(@RequestBody Menu newMenu, NativeWebRequest request) throws MenuNotFoundException, MenuAlreadyExistsException { 
		Menu menu = newMenu ;
		if( newMenu.getMenuId() > 0 ) {
			menu = menuService.getMenuById(newMenu.getMenuId());		
			if (!org.apache.commons.lang3.StringUtils.equals(newMenu.getName(), menu.getName())) {
				menu.setName(newMenu.getName());
			} 
			if (!org.apache.commons.lang3.StringUtils.equals(newMenu.getDescription(), menu.getDescription())) {
				menu.setDescription(newMenu.getDescription());
			}
		}
		menuService.saveOrUpdateMenu(menu); 
		Result result = Result.newResult("menu", menu);
		return result ;
    }	
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getImageProperties (
		@PathVariable Long menuId, 
		NativeWebRequest request) throws NotFoundException {
		
		Menu menu = menuService.getMenuById(menuId);
		Map<String, String> properties = menu.getProperties(); 
		
		return Utils.toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId:[\\p{Digit}]+}/properties/update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> updateImageProperties (
		@PathVariable Long menuId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		
		Menu menu = menuService.getMenuById(menuId);
		Map<String, String> properties = menu.getProperties();   
		// update or create
		for (Property property : newProperties) {
		    properties.put(property.getName(), property.getValue().toString());
		} 
		menuService.saveOrUpdateMenu(menu); 
		return Utils.toList(menu.getProperties());
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> deleteImageProperties (
		@PathVariable Long menuId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		Menu menu = menuService.getMenuById(menuId);
		Map<String, String> properties = menu.getProperties();  
		for (Property property : newProperties) {
		    properties.remove(property.getName());
		}
		menuService.saveOrUpdateMenu(menu); 
		return Utils.toList(menu.getProperties());
	} 
	
	/**
	 * MENU ITEMS API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId}/items/list.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public ItemList getMenuItems(
    		@PathVariable Long menuId, 
    		@RequestParam(value = "widget", defaultValue = "", required = false) String widget,
    		@RequestBody DataSourceRequest dataSourceRequest,
    		NativeWebRequest request) throws NotFoundException {		
    		
		Menu menu = menuService.getMenuById(menuId);   
		dataSourceRequest.getParameters().add(  new ParameterValue(1, "MENU_ID", Types.NUMERIC, menu.getMenuId() ) ) ;
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_MENU_ITEM_IDS_BY_MENU_ID_AND_REQUEST");
    		List<Long> itemIds = customQueryService.list(dataSourceRequest, Long.class);
    		List<MenuItem> items = new ArrayList<MenuItem>(itemIds.size());
    		for( Long itemId : itemIds ) {
    			MenuItem item = menuService.getMenuItemById(itemId);
    			if(StringUtils.isNotEmpty(widget) && StringUtils.equals("treelist", widget)) {
    				if( item.getParentMenuItemId() != null && item.getParentMenuItemId() < 1 )
    					item.setParentMenuItemId(null);
    			}
    			items.add(item);
    		}
    		return new ItemList(items, items.size());	
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId}/items/create.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result createMenuItem(@PathVariable Long menuId, @RequestBody MenuItem newMenuItem, NativeWebRequest request) throws MenuNotFoundException, MenuAlreadyExistsException { 
		
		Menu menu = menuService.getMenuById(menuId);	
		if( newMenuItem.getMenuId() != menu.getMenuId()) {
			newMenuItem.setMenuId(menu.getMenuId());
		}
		menuService.saveOrUpdateMenuItem(newMenuItem);
		Result r = Result.newResult();
		return Result.newResult();
    }

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId}/items/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result saveOrUpdateMenuItem(@PathVariable Long menuId, @RequestBody MenuItem newMenuItem, NativeWebRequest request) throws MenuNotFoundException, MenuAlreadyExistsException, MenuItemNotFoundException { 
		
		Menu menu = menuService.getMenuById(menuId);
		MenuItem menuItem ;
		if( newMenuItem.getMenuItemId() > 0 )
		{
			menuItem = menuService.getMenuItemById(newMenuItem.getMenuItemId());	 
			if (!org.apache.commons.lang3.StringUtils.equals(newMenuItem.getName(), menuItem.getName())) {
				menuItem.setName(newMenuItem.getName());
			} 
			if (!org.apache.commons.lang3.StringUtils.equals(newMenuItem.getDescription(), menuItem.getDescription())) {
				menuItem.setDescription(newMenuItem.getDescription());
			}
			if (!org.apache.commons.lang3.StringUtils.equals(newMenuItem.getLocation(), menuItem.getLocation())) {
				menuItem.setLocation(newMenuItem.getLocation());
			}
			if (newMenuItem.getParentMenuItemId()!= menuItem.getParentMenuItemId()) {
				menuItem.setParentMenuItemId(newMenuItem.getParentMenuItemId());
			}
			if (newMenuItem.getSortOrder() != menuItem.getSortOrder()) {
				menuItem.setSortOrder(newMenuItem.getSortOrder());
			}
			menuItem.setPage(newMenuItem.getPage());
			menuItem.setRoles(newMenuItem.getRoles());
			
		}else {
			menuItem = newMenuItem ;
			if( newMenuItem.getMenuId() != menu.getMenuId())
				newMenuItem.setMenuId(menu.getMenuId());
		}
		menuService.saveOrUpdateMenuItem(menuItem);
		menuService.refresh(menu);
		return Result.newResult(); 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId}/items/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result deleteMenuItem(@PathVariable Long menuId, @RequestBody MenuItem newMenuItem, NativeWebRequest request) throws MenuNotFoundException, MenuAlreadyExistsException, MenuItemNotFoundException { 
		Menu menu = menuService.getMenuById(menuId);	 
		if( newMenuItem.getMenuItemId() > 0 )
		{
			MenuItem menuItem = menuService.getMenuItemById(newMenuItem.getMenuItemId());	
			menuService.deleteMenuItem(menuItem);
			menuService.refresh(menu);
		}
		return Result.newResult(); 
	}	
	

	
	/** ----------------------------------
	 * MENU ITEM PROPERTY API
	 * ----------------------------------- **/
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId:[\\p{Digit}]+}/items/{itemId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getImageProperties (
		@PathVariable Long menuId, 
		@PathVariable Long itemId, 
		NativeWebRequest request) throws NotFoundException {
		MenuItem menuToUse = 	menuService.getMenuItemById(itemId);
		Map<String, String> properties = menuToUse.getProperties(); 
		return Utils.toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId:[\\p{Digit}]+}/items/{itemId:[\\p{Digit}]+}/properties/update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> updateImageProperties (
		@PathVariable Long menuId, 		
		@PathVariable Long itemId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		MenuItem menuToUse = 	menuService.getMenuItemById(itemId);
		Map<String, String> properties = menuToUse.getProperties();   
		// update or create
		for (Property property : newProperties) {
		    properties.put(property.getName(), property.getValue().toString());
		} 
		menuService.saveOrUpdateMenuItem(menuToUse); 
		return Utils.toList(menuToUse.getProperties());
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/menus/{menuId:[\\p{Digit}]+}/items/{itemId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> deleteImageProperties (
		@PathVariable Long menuId, 			
		@PathVariable Long itemId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		MenuItem menuToUse = 	menuService.getMenuItemById(itemId);
		Map<String, String> properties = menuToUse.getProperties();  
		for (Property property : newProperties) {
		    properties.remove(property.getName());
		}
		menuService.saveOrUpdateMenuItem(menuToUse);
		return Utils.toList(menuToUse.getProperties());
	}
}
