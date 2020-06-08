package architecture.community.user;

import java.util.List;

public interface RoleManager {

	public abstract int getRoleCount();
	
	public abstract List<Role> getRoles();
	
	public abstract Role createRole(String name) throws RoleAlreadyExistsException  ;
	
	public abstract Role createRole(String name, String description) throws RoleAlreadyExistsException ;
	
	public void updateRole(Role role) throws RoleNotFoundException ;
	
	public void deleteRole(Role role) throws RoleNotFoundException ;
	
	public Role getRole(String name) throws RoleNotFoundException; ;
	
	public Role getRole(long roleId) throws RoleNotFoundException;;
	
	public abstract List<Role> getFinalUserRoles(long userId);
	
	public List<Role> getUserRoles(User user);
	
	public abstract void grantRole(Role role, User user);
	
	public abstract void revokeRole(Role role, User user);
	
}
