package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.model.Property;
import architecture.community.query.CustomQueryService;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.security.spring.acls.CommunityPermissions;
import architecture.community.security.spring.acls.ObjectAccessControlEntry;
import architecture.community.user.AvatarImage;
import architecture.community.user.CommunityUser;
import architecture.community.user.DefaultRole;
import architecture.community.user.EmailAlreadyExistsException;
import architecture.community.user.Role;
import architecture.community.user.RoleAlreadyExistsException;
import architecture.community.user.RoleManager;
import architecture.community.user.RoleNotFoundException;
import architecture.community.user.User;
import architecture.community.user.UserAlreadyExistsException;
import architecture.community.user.UserAvatarService;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.user.UserProvider;
import architecture.community.user.UserTemplate;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.util.StringUtils; 

@Controller("community-mgmt-security-secure-data-controller")
@RequestMapping("/data/secure/mgmt/security")
public class SecurityDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	
	@Autowired(required = false) 
	@Qualifier("userManager")
	private UserManager userManager;
	
	
	@Autowired(required = false) 
	private List<UserProvider> userProvisers;
	
	@Autowired(required = false) 
	@Qualifier("userAvatarService")
	private UserAvatarService userAvatarService;
	
	
	@Autowired(required = false) 
	@Qualifier("roleManager")
	private RoleManager roleManager;
	
	@Autowired(required = false) 
	@Qualifier("aclService")
	private CommunityAclService aclService;	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/find.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList findUsers (
		@RequestBody DataSourceRequest dataSourceRequest,
		NativeWebRequest request) throws NotFoundException {	
				 
		if( dataSourceRequest.getPageSize() == 0)
			dataSourceRequest.setPageSize(30);
		dataSourceRequest.setStatement("COMMUNITY_USER.COUNT_USERS_BY_REQUEST");	
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		
		//customQueryService.list(dataSourceRequest);
		
		List<User> users = new ArrayList<User>(totalCount);
		if( totalCount > 0) {
			dataSourceRequest.setStatement("COMMUNITY_USER.FIND_USER_IDS_BY_REQUEST");		
			List<Long> userIds = customQueryService.list(dataSourceRequest, Long.class);
			for( Long userId : userIds ) {
				try {
					users.add(userManager.getUser(userId));
				} catch (UserNotFoundException e) {
				}
			}
		}
		return new ItemList(users, totalCount);
	}
	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/providers/{name}/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList findProviderUsers (
		@PathVariable String name,
		@RequestBody DataSourceRequest dataSourceRequest,
		NativeWebRequest request) throws NotFoundException {	
		
		
		UserProvider provider = getUserProviderByName(name);
		if( provider.getType() == UserProvider.Type.JDBC ) {
			// using query ...
			log.debug("extract user with external '{}'", name );
		}
		
		return provider.findUsers(dataSourceRequest, userManager);
	}
	
	private UserProvider getUserProviderByName(String name) {
		UserProvider provider = null ;
		for(UserProvider p : userProvisers ){
			if(StringUtils.equals(p.getName(), name))
				provider = p;
		}
		return provider;
	} 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/providers/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList listUserProvider (
		@RequestParam(value = "enabled", defaultValue = "true", required = false) Boolean enabled, 	
		NativeWebRequest request) throws NotFoundException {			 
		
		List<UserProviderInfo> list = new ArrayList<UserProviderInfo>();
		for(UserProvider p : userProvisers ){
			list.add(new UserProviderInfo(p));
		}
		return new ItemList(list, list.size());
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result saveOrUpdateUser(@RequestBody CommunityUser user , NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException, EmailAlreadyExistsException { 
		
		log.debug("Save or update user {} ", user.toString());
		User userToUse = user ;
		if( userToUse.getUserId() > 0 ) {
			userManager.updateUser(userToUse);
		}else {
			userToUse = userManager.createUser(userToUse);
		}
		return Result.newResult("item", userToUse);
    }

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result deleteUsers(@RequestBody List<Long> users , NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException, EmailAlreadyExistsException { 
		
		log.debug("Delete users {} ", users);
		List<User> list = new ArrayList<User>(users.size());
		for(Long id : users) {
			list.add(new UserTemplate(id));
		}
		
		Result result = Result.newResult("item", users); 
		userManager.deleteUsers(list); 
		result.setCount(users.size()); 
		return result;
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public User getUser(
		@PathVariable Long userId, 
		NativeWebRequest request) throws UserNotFoundException {
		return userManager.getUser(userId);
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/avatar/upload.json", method = RequestMethod.POST)
	@ResponseBody
	public Result uploadMyAvatarImage(@PathVariable Long userId, MultipartHttpServletRequest request) throws IOException, UserNotFoundException {
		Result result = Result.newResult();
		result.setAnonymous(false);
		User user = userManager.getUser(userId);
		AvatarImage imageToUse = new AvatarImage(user);
		Iterator<String> names = request.getFileNames();
		while (names.hasNext()) {
			String fileName = names.next();
			MultipartFile mpf = request.getFile(fileName);
			InputStream is = mpf.getInputStream();
			log.debug("upload  file:{}, size:{}, type:{} ", mpf.getOriginalFilename(), mpf.getSize(), mpf.getContentType());
			imageToUse.setFilename(mpf.getOriginalFilename());
			imageToUse.setImageContentType(mpf.getContentType());
			imageToUse.setImageSize((int) mpf.getSize());
			
			userAvatarService.addAvatarImage(imageToUse, is, user);
			
			result.setCount(result.getCount() + 1);
		}
		return result;
	}
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getUserProperties(@PathVariable Long userId, NativeWebRequest request)
			throws UserNotFoundException { 
		if (userId <= 0) {
			return Collections.EMPTY_LIST;
		}		
		User user = userManager.getUser(userId);
		Map<String, String> properties = user.getProperties();
		return toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/properties/update.json", method = RequestMethod.POST)
	@ResponseBody
	public List<Property> updateUserProperties(
			@PathVariable Long userId,
			@RequestBody List<Property> newProperties, 
			NativeWebRequest request) throws UserNotFoundException, UserAlreadyExistsException {
	 
		User user = userManager.getUser(userId);
		Map<String, String> properties = user.getProperties();
		// update or create
		for (Property property : newProperties) {
			properties.put(property.getName(), property.getValue().toString());
		}
		if (newProperties.size() > 0) {
			userManager.updateUser(user);
		}
		return toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.DELETE })
	@ResponseBody
	public List<Property> deleteUserProperties(
			@PathVariable Long userId, 
			@RequestBody List<Property> newProperties, 
			NativeWebRequest request) throws NotFoundException, UserNotFoundException, UserAlreadyExistsException { 
		User user = userManager.getUser(userId);
		Map<String, String> properties = user.getProperties();
		for (Property property : newProperties) {
			properties.remove(property.getName());
		}
		if (newProperties.size() > 0) {
			userManager.updateUser(user);
		}
		return toList(properties);
	}

	protected List<Property> toList(Map<String, String> properties) {
		List<Property> list = new ArrayList<Property>();
		for (String key : properties.keySet()) {
			String value = properties.get(key);
			list.add(new Property(key, value));
		}
		return list;
	} 
	
	public static class UserProviderInfo implements Serializable {
		
		String name ;
		boolean enabled ;
		boolean paginationable ;
		boolean updatable;
		
		public UserProviderInfo(UserProvider p) {
			this.name = p.getName();
			this.paginationable = p.supportsPagination();
			this.enabled = p.isEnabled();
			this.updatable = p.supportsUpdate();		
		}

		public String getName() {
			return name;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public boolean isPaginationable() {
			return paginationable;
		}

		public boolean isUpdatable() {
			return updatable;
		}
		
		
	}
	
	
	/**
	 * USER & ROLE API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/roles/list.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public ItemList getUserRoles(@PathVariable Long userId, NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException {
		ItemList result = new ItemList();
		if( userId > 0 ) {
			User user = userManager.getUser(userId);
			List<Role> items = roleManager.getFinalUserRoles(user.getUserId());
			result.setItems(items);
			result.setTotalCount(items.size());
		}
		return result;
    }

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/roles/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result saveOrUpdateUserRoles(@PathVariable Long userId, @RequestBody List<DefaultRole> roles, NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException, RoleNotFoundException {
		
		log.debug("save or update user {} roles {}", userId, roles);
		
		//ItemList result = new ItemList();
		if( userId > 0 && roles != null ) {
			User user = userManager.getUser(userId);
			
			// requested final roles.
			List<Role> list1 = new ArrayList<Role> (roles.size());
			for(Role r : roles ) {
				list1.add( roleManager.getRole(r.getRoleId() ));
			}
			
			// granted user roles..
			List<Role> granted = roleManager.getFinalUserRoles(user.getUserId()); 
			// revoke roles
			if( list1.size() > 0 ) {
				for( Role r : granted) {
					if( !list1.contains(r) ) {
						log.debug("revoke role -{}, user - {}", r.getRoleId(), user.getUserId());
						roleManager.revokeRole(r, user);
					}
				} 
				// add roles..
				for( Role r : list1) {
					if( !granted.contains(r)) {
						log.debug("grant role -{}, user - {}", r.getRoleId(), user.getUserId());
						roleManager.grantRole(r, user);
					}
				}
			}else {
				for( Role r : granted) {
					log.debug("revoke role -{}, user - {}", r.getRoleId(), user.getUserId());
					roleManager.revokeRole(r, user);
				}
			}
		}
		
		return Result.newResult();
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/roles/add.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result addUserRoles(@PathVariable Long userId, @RequestParam(value = "roleId", defaultValue = "0", required = false) Long roleId, NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException, RoleNotFoundException {
		
		//ItemList result = new ItemList();
		if( userId > 0 && roleId > 0 ) {
			User user = userManager.getUser(userId);
			Role role = roleManager.getRole(roleId);
			roleManager.grantRole(role, user);			
			//List<Role> items = roleManager.getFinalUserRoles(user.getUserId());
			//result.setItems(items);
			//result.setTotalCount(items.size());
		}	
		return Result.newResult();
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/users/{userId:[\\p{Digit}]+}/roles/remove.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result removeUserRoles(@PathVariable Long userId, @RequestParam(value = "roleId", defaultValue = "0", required = false) Long roleId, NativeWebRequest request) throws  UserNotFoundException, UserAlreadyExistsException, RoleNotFoundException {
		
		//ItemList result = new ItemList();
		if( userId > 0 && roleId > 0 ) {
			User user = userManager.getUser(userId);
			Role role = roleManager.getRole(roleId);
			roleManager.revokeRole(role, user);			
			//List<Role> items = roleManager.getFinalUserRoles(user.getUserId());
			//result.setItems(items);
			//result.setTotalCount(items.size());
		}	
		return Result.newResult();
    }
	

	/**
	 * ROLE API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/roles/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList getRoles (NativeWebRequest request) {					
		List<Role> roles = roleManager.getRoles();		
		return new ItemList(roles, roles.size());	
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/roles/create.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result createRole(@RequestBody DefaultRole newRole, NativeWebRequest request) throws RoleNotFoundException, RoleAlreadyExistsException { 
		roleManager.createRole(newRole.getName(), newRole.getDescription());
		return Result.newResult();
    }

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/roles/update.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result updateRole(@RequestBody DefaultRole newRole, NativeWebRequest request) throws RoleNotFoundException, RoleAlreadyExistsException {
 
		log.debug("update role ({}) {}", newRole.getRoleId(), newRole.getName());
		DefaultRole role = (DefaultRole) roleManager.getRole(newRole.getRoleId());
		if (!org.apache.commons.lang3.StringUtils.equals(newRole.getName(), role.getName())) {
		    role.setName(newRole.getName());
		} 
		if (!org.apache.commons.lang3.StringUtils.equals(newRole.getDescription(), role.getDescription())) {
		    role.setDescription(newRole.getDescription());
		}
		roleManager.updateRole(role);
		return Result.newResult();
    }
	
	
	/**
	 * PERMISSIONS API 
	 ******************************************/
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/permissions/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList getPermissions (NativeWebRequest request) {	
		
		List<CommunityPermissions> list = new ArrayList<CommunityPermissions>();		
		for( CommunityPermissions p : CommunityPermissions.values() ) {
			list.add(p);
		}		
		return new ItemList(list, list.size());		
	}
	
	
	/**
	 * 
	 * @param objectType
	 * @param objectId
	 * @param request
	 * @return
	 */
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/permissions/{objectType:[\\p{Digit}]+}/{objectId:[\\p{Digit}]+}/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList getAssignedPermissions (@PathVariable Integer objectType, @PathVariable Long objectId, NativeWebRequest request) {			
		
		List<AccessControlEntry> entries = aclService.getAsignedPermissions(Models.valueOf(objectType).getObjectClass(), objectId);
		List<ObjectAccessControlEntry> list = new ArrayList<ObjectAccessControlEntry>(entries.size());
		for( AccessControlEntry entry : entries )
			list.add(new ObjectAccessControlEntry(entry));		
		return new ItemList(list, list.size());		
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/permissions/{objectType:[\\p{Digit}]+}/{objectId:[\\p{Digit}]+}/add.json", method = { RequestMethod.POST})
	@ResponseBody
	public Result addPermission(@PathVariable Integer objectType, @PathVariable Long objectId, @RequestBody ObjectAccessControlEntry entry ) throws UserNotFoundException, RoleNotFoundException {	
		Result result = Result.newResult();
		if(org.apache.commons.lang3.StringUtils.equals(entry.getGrantedAuthority(), "USER")){
			if(StringUtils.isNullOrEmpty(entry.getGrantedAuthorityOwner()) || org.apache.commons.lang3.StringUtils.equals(entry.getGrantedAuthorityOwner(), SecurityHelper.ANONYMOUS.getUsername()) ) { 
				aclService.addAnonymousPermission(Models.valueOf(objectType).getObjectClass(), objectId, CommunityPermissions.getPermissionByName(entry.getPermission())); 
			}else {
				User user = userManager.getUser(entry.getGrantedAuthorityOwner()); 
				aclService.addPermission(Models.valueOf(objectType).getObjectClass(), objectId, user, CommunityPermissions.getPermissionByName(entry.getPermission()));
			
			}		
		}else if (org.apache.commons.lang3.StringUtils.equals(entry.getGrantedAuthority(), "ROLE")){
			Role role = roleManager.getRole(entry.getGrantedAuthorityOwner());
			aclService.addPermission(Models.valueOf(objectType).getObjectClass(), objectId, role, CommunityPermissions.getPermissionByName(entry.getPermission()));
		}
		return result;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM" })
	@RequestMapping(value = "/permissions/{objectType:[\\p{Digit}]+}/{objectId:[\\p{Digit}]+}/remove.json", method = { RequestMethod.POST})
	@ResponseBody
	public Result removePermission(@PathVariable Integer objectType, @PathVariable Long objectId, @RequestBody ObjectAccessControlEntry entry ) throws UserNotFoundException, RoleNotFoundException {		
		Result result = Result.newResult();
		if(org.apache.commons.lang3.StringUtils.equals(entry.getGrantedAuthority(), "USER")){
			if(StringUtils.isNullOrEmpty(entry.getGrantedAuthorityOwner()) || org.apache.commons.lang3.StringUtils.equals(entry.getGrantedAuthorityOwner(), SecurityHelper.ANONYMOUS.getUsername()) ) {
				aclService.removeAnonymousPermission(Models.valueOf(objectType).getObjectClass(), objectId, CommunityPermissions.getPermissionByName(entry.getPermission()));
			}else {
				User user = userManager.getUser(entry.getGrantedAuthorityOwner());
				aclService.removePermission(Models.valueOf(objectType).getObjectClass(), objectId, user, CommunityPermissions.getPermissionByName(entry.getPermission()));
			}		
		}else if (org.apache.commons.lang3.StringUtils.equals(entry.getGrantedAuthority(), "ROLE")){
			Role role = roleManager.getRole(entry.getGrantedAuthorityOwner());
			aclService.removePermission(Models.valueOf(objectType).getObjectClass(), objectId, role, CommunityPermissions.getPermissionByName(entry.getPermission()));
		}
		return result;
	}	
}