package architecture.community.security.spring.acls;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import architecture.community.user.Role;
import architecture.community.user.User;

public interface CommunityAclService extends MutableAclService { 
	
	
	public <T> void getFinalGrantedPermissions(  Authentication authentication, Class<T> clazz, Serializable identifier, PermissionsSetter setter ) ;
	
	
	
	public <T> boolean isPermissionGrantedFinally(Authentication authentication, Class<T> clazz, Serializable identifier, List<Permission> permissions) ;
	
	/**
	 * 
	 * @param authentication
	 * @param identity
	 * @param permissions
	 * @return
	 */
	public <T> boolean isPermissionGrantedFinally(Authentication authentication, ObjectIdentity identity, List<Permission> permissions) ;
	

	/**
	 * 지정된 객체에 대하여 부여된 모든 권한 정보를 리턴한다.
	 * 
	 * @param clazz
	 * @param identifier
	 * @return
	 */
	public <T> List<AccessControlEntry> getAsignedPermissions(Class<T> clazz, Serializable identifier) ;
	
	/**
	 * 지정된 객체에 대한 권한이 있는가를 리턴한다.
	 * 사용자에게 부여된 롤에 대한 권한 역시 동시에 검사하여 결과에 반영한다.
	 * 
	 * @param clazz
	 * @param identifier
	 * @param user
	 * @param permissions
	 * @return
	 */
	public <T> boolean isPermissionGranted(Class<T> clazz, Serializable identifier, UserDetails user, Permission... permissions);
	
	public <T> boolean isPermissionGrantedToAnonymous(Class<T> clazz, Serializable identifier, Permission... permissions) ;
	
 
	public <T> void addAnonymousPermission(Class<T> clazz, Serializable identifier, Permission permission) ;
 
	public <T> void removeAnonymousPermission(Class<T> clazz, Serializable identifier, Permission permission) ;
	 
	public <T> void addPermission(Class<T> clazz, Serializable identifier, User user, Permission permission) ;
 
	public <T> boolean isPermissionGranted(Class<T> clazz, Serializable identifier, User user, Permission... permissions) ;
 
	public <T> void removePermission(Class<T> clazz, Serializable identifier, User user, Permission permission) ;
	 
	public <T> void addPermission(Class<T> clazz, Serializable identifier, Role role, Permission permission);

	public <T> boolean isPermissionGranted(Class<T> clazz, Serializable identifier, Role role, Permission... permissions);
 
	public <T> void removePermission(Class<T> clazz, Serializable identifier, Role role, Permission permission);
	
	public PermissionsBundle getPermissionBundle( Authentication authentication, Class objectType , long objectId );
	
	public PermissionsBundle getPermissionBundle( Object object );
    
}
