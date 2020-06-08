package architecture.community.user.dao;

import java.util.List;

import architecture.community.user.Role;

public interface RoleDao {
	
	public abstract void createRole(Role role);
	
	public abstract void updateRole(Role role);
	
	public abstract void deleteRole(Role role);
	
	public abstract Role getRoleById(long roleId);
	
	public abstract Role getRoleByName(String name, boolean caseSensetive);
		
	public abstract List<Long> getAllRoleIds();
	
	public abstract int getRoleCount();
	
	public abstract List<Long> getUserRoleIds(long userId);
	
	public abstract void removeUserRoles(long userId);
	
	public abstract void removeUserRole(long roleId, long userId);
	
	public abstract void addUserRole(long roleId, long userId);
	
}
