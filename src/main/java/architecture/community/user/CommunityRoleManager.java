package architecture.community.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.eventbus.Subscribe;

import architecture.community.user.dao.RoleDao;
import architecture.community.user.event.UserRemovedEvent;

@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommunityRoleManager implements RoleManager {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private com.google.common.cache.LoadingCache<Long, Role> roleCache = null;
	
	private com.google.common.cache.LoadingCache<String, Long> roleIdCache = null;
	
	private com.google.common.cache.LoadingCache<Long, List<Long>> userRoleIdsCache = null;
	
	
	@Subscribe
	@EventListener 
	@Async
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void handelUserRemovedEvent(UserRemovedEvent e) {
		logger.debug("User romoved. Revoke all granted roles for '{}'" , e.getUser().getUsername() );
		User user = e.getUser();
		invalidateUserRoleIdsCache(user.getUserId());
		roleDao.removeUserRoles(user.getUserId());
	}
	
	@PostConstruct
	public void initialize(){		
		logger.debug("creating cache ...");		
		roleCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, Role>(){			
					public Role load(Long roleId) throws Exception {
						//logger.debug("get role form database by {}", roleId );
						return roleDao.getRoleById(roleId);
				}}
			);
		
		roleIdCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<String, Long>(){			
					public Long load(String name) throws Exception {	
						//logger.debug("get role form database by {}", name );
						Role role = roleDao.getRoleByName(name, caseInsensitiveRoleNameMatch);					
						return role.getRoleId();
				}}
		);		
		userRoleIdsCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, List<Long>>(){			
					public List<Long> load(Long userId) throws Exception {	
						//logger.debug("get role form database by {}", name );
						return roleDao.getUserRoleIds(userId);	
				}}
		);
		
	}
	
	@Inject
	@Qualifier("roleDao")
	private RoleDao roleDao;
		
	private boolean caseInsensitiveRoleNameMatch = false;
	
	public Role getRole(String name) throws RoleNotFoundException {
		String nameToUse = caseRoleName(name);
		Long roleId;
		try {
			roleId = roleIdCache.get(nameToUse);
		} catch (Throwable e) {
			throw new RoleNotFoundException(e);
		}		
		return getRole(roleId);
	}

	public Role getRole(long roleId) throws RoleNotFoundException {		
		try {
			
			Role role = roleCache.get(roleId);			
			if( roleIdCache.getIfPresent(role.getName()) == null ){
				logger.debug("put ID:{}, NAME:{} into roleIdcache. ", role.getRoleId(), role.getName());
				roleIdCache.put(role.getName(), role.getRoleId());
			}
			
			return role;
		} catch (ExecutionException e) {
			throw new RoleNotFoundException(e);
		}
	}

	public int getRoleCount() {
		return roleDao.getRoleCount();
	}

	public List<Role> getRoles() {
		List<Role> roles = new ArrayList<Role>();
		List<Long> roleIds = roleDao.getAllRoleIds();
		for (long roleId : roleIds) {
		    try {
		    	roles.add(getRole(roleId));
		    } catch (RoleNotFoundException e) {
		    }
		}
		return roles;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Role createRole(String name) throws RoleAlreadyExistsException {		
		return createRole(name, null);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Role createRole(String name, String description) throws RoleAlreadyExistsException  {		
		
		try {
			getRole(name);
			throw new RoleAlreadyExistsException();
		} catch (RoleNotFoundException e) {}
		
		Date now = new Date();
		DefaultRole newRole = new DefaultRole();
		newRole.setName(name);
		newRole.setDescription(description);
		newRole.setCreationDate(now);
		newRole.setModifiedDate(now);
		
		roleDao.createRole(newRole);		
		
		return newRole;
		
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateRole(Role role) {
		invalidateRoleCache(role);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteRole(Role role) {
		invalidateRoleCache(role);
	}

	@Override
	public List<Role> getFinalUserRoles(long userId) {
		logger.debug("load final user roles {}" , userId);
		List<Role> roles = new ArrayList<Role>();
		roles.addAll(getUserRoles(new UserTemplate(userId)));
		return roles;
	}

	public List<Role> getUserRoles(User user){		
		List<Long> roleIds = getUserRoleIds(user.getUserId());
		List<Role> roles = new ArrayList<Role>(roleIds.size());
		for (long roleId : roleIds) {
		    try {
		    	roles.add(getRole(roleId));
		    } catch (RoleNotFoundException e) {
		    }
		}		
		return roles;
	}
	
	private List<Long> getUserRoleIds(Long userId){
		try {					
			return userRoleIdsCache.get(userId);			
		} catch (ExecutionException e) {
			logger.error("", e);
			return Collections.emptyList();
		}
	}
	
	private void invalidateUserRoleIdsCache(Long userId){
		userRoleIdsCache.invalidate(userId);
	}
	
	private void invalidateRoleCache(Role role){		
		roleCache.invalidate(role.getRoleId());
		roleIdCache.invalidate(role.getName());
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void grantRole(Role role, User user) {
		roleDao.addUserRole(role.getRoleId(), user.getUserId());
		invalidateUserRoleIdsCache(user.getUserId());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void revokeRole(Role role, User user) {		
		roleDao.removeUserRole(role.getRoleId(), user.getUserId());	
		invalidateUserRoleIdsCache(user.getUserId());
	}
	
	private String caseRoleName(String name) {
		return caseInsensitiveRoleNameMatch ? name.toUpperCase() : name;
	} 
}
