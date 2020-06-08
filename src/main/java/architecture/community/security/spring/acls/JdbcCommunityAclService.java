package architecture.community.security.spring.acls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.acls.model.UnloadedSidException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.user.Role;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;

public class JdbcCommunityAclService extends JdbcMutableAclService implements CommunityAclService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
	
	public JdbcCommunityAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
		super(dataSource, lookupStrategy, aclCache);
	}

	
	/**
	 * 
	 */
	public void initialzie() {

		this.setClassIdentityQuery("classIdentityQuery");
		this.setClassPrimaryKeyQuery("selectClassPrimaryKey");
		this.setDeleteEntryByObjectIdentityForeignKeySql("deleteEntryByObjectIdentityForeignKey");
		this.setDeleteObjectIdentityByPrimaryKeySql("deleteObjectIdentityByPrimaryKey");
		this.setFindChildrenQuery("findChildrenSql");
		this.setForeignKeysInDatabase(false);
		this.setInsertClassSql("insertClass");
		this.setInsertEntrySql("insertEntry");
		this.setInsertObjectIdentitySql("insertObjectIdentity");
		this.setInsertSidSql("insertSid");
		this.setObjectIdentityPrimaryKeyQuery("selectObjectIdentityPrimaryKey");
		this.setSidIdentityQuery("sidIdentityQuery");
		this.setSidPrimaryKeyQuery("selectSidPrimaryKey");
		this.setUpdateObjectIdentity("updateObjectIdentity");
		
	}
	
	public <T> void getFinalGrantedPermissions(  Authentication authentication, Class<T> clazz, Serializable identifier, PermissionsSetter setter ) {	
		
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		
		log.debug("final granted permission for {}({})" ,  clazz.getCanonicalName() ,  identifier );
		
		MutableAcl acl = null ;
		List<Sid> sids = Collections.EMPTY_LIST;
		try {
			acl = (MutableAcl) readAclById(identity);
			log.debug("final granted permission entries: {}" , acl.getEntries() );
			sids = sidRetrievalStrategy.getSids(authentication);	
			// checking anonymous !!
			boolean isAnonymous = false;
			if (authentication.getPrincipal() instanceof CommuintyUserDetails ){
				isAnonymous = ((CommuintyUserDetails) authentication.getPrincipal()).getUser().isAnonymous() ;
			}
			
			if( ! isAnonymous ) {
				sids.add(new PrincipalSid(SecurityHelper.ANONYMOUS_USER_DETAILS.getUser().getUsername()));
			}
		} catch (NotFoundException e) {
		}
		
		setter.execute( sids, acl);	
	
	}
	
	
	
	public <T> boolean isPermissionGrantedFinally(Authentication authentication, Class<T> clazz, Serializable identifier, List<Permission> permissions) {		 
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		log.debug("checking final permissions for {}", identity);
		return isPermissionGrantedFinally(authentication, identity, permissions);
	}
	/**
	 * 
	 * @param authentication
	 * @param identity
	 * @param permissions
	 * @return
	 */
	public <T> boolean isPermissionGrantedFinally(Authentication authentication, ObjectIdentity identity, List<Permission> permissions) {		
			
		boolean isGranted = false;			
		MutableAcl acl;
		
		try {
			acl = (MutableAcl) readAclById(identity);
		} catch (NotFoundException e1) {
			return isGranted ;
		}
		
		List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
		boolean isAnonymous = false;
		List<Permission> permissionsToUse = permissions;
		// checking anonymous !!
		if (authentication.getPrincipal() instanceof CommuintyUserDetails ){
			isAnonymous = ((CommuintyUserDetails) authentication.getPrincipal()).getUser().isAnonymous() ;
		}
		if( !isAnonymous ) {
			sids.add(new PrincipalSid(SecurityHelper.ANONYMOUS_USER_DETAILS.getUser().getUsername()));
		}
					
		try { 
			if (!permissions.contains(CommunityPermissions.ADMINISTRATION) ) {
				isGranted = acl.isGranted(Arrays.asList( (Permission)CommunityPermissions.ADMINISTRATION) , sids, false);				
			}
			
			log.debug( "Checking permissions {} for {}", permissionsToUse, sids );
			if( isGranted ) {
				isGranted = acl.isGranted(permissionsToUse, sids, false);
			}
			
		} catch (NotFoundException e) {
			log.warn("Unable to find an ACE for the given object", e);
		} catch (UnloadedSidException e) {
			log.error("Unloaded Sid", e);
		}

		return isGranted;
	}
	

	/**
	 * 지정된 객체에 대하여 부여된 모든 권한 정보를 리턴한다.
	 * 
	 * @param clazz
	 * @param identifier
	 * @return
	 */
	public <T> List<AccessControlEntry> getAsignedPermissions(Class<T> clazz, Serializable identifier) {
		try {
			ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
			MutableAcl acl = (MutableAcl) readAclById(identity);		
			List<AccessControlEntry> entries = acl.getEntries();
			return entries;
		} catch (NotFoundException e) {
			return Collections.EMPTY_LIST;
		}
	}
	
	
	
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
	public <T> boolean isPermissionGranted(Class<T> clazz, Serializable identifier, UserDetails user, Permission... permissions) {		
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		boolean isGranted = false;			
		MutableAcl acl;
		try {
			acl = (MutableAcl) readAclById(identity);
		} catch (NotFoundException e1) {
			return isGranted ;
		}		

		List<Sid> list = new ArrayList<Sid>();
		list.add(new PrincipalSid(user.getUsername()));
		
		for( GrantedAuthority authority : user.getAuthorities() ) {
			list.add(new GrantedAuthoritySid(authority.getAuthority()));
		}		
				
		try {
			isGranted = acl.isGranted(Arrays.asList(permissions), list, false);
		} catch (NotFoundException e) {
			log.warn("Unable to find an ACE for the given object", e);
		} catch (UnloadedSidException e) {
			log.error("Unloaded Sid", e);
		}

		return isGranted;
	}
	
	public <T> boolean isPermissionGrantedToAnonymous(Class<T> clazz, Serializable identifier, Permission... permissions) {	
		return isPermissionGranted(clazz, identifier, SecurityHelper.ANONYMOUS_USER_DETAILS , permissions);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> void addAnonymousPermission(Class<T> clazz, Serializable identifier, Permission permission) {	
		 addPermission(clazz, identifier, SecurityHelper.ANONYMOUS_USER_DETAILS.getUser() , permission);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> void removeAnonymousPermission(Class<T> clazz, Serializable identifier, Permission permission) {	
		removePermission(clazz, identifier, SecurityHelper.ANONYMOUS_USER_DETAILS.getUser() , permission);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> void addPermission(Class<T> clazz, Serializable identifier, User user, Permission permission) {
		ObjectIdentity identity = new ObjectIdentityImpl(clazz, identifier);
		MutableAcl acl = isNewAcl(identity);
		isPermissionGranted(permission, new PrincipalSid(user.getUsername()), acl);
		updateAcl(acl);
	}


	public <T> boolean isPermissionGranted(Class<T> clazz, Serializable identifier, User user, Permission... permissions) {
		Sid sid = new PrincipalSid(user.getUsername());
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		MutableAcl acl = (MutableAcl) readAclById(identity);		

		boolean isGranted = false;
		try {
			isGranted = acl.isGranted(Arrays.asList(permissions), Arrays.asList(sid), false);
		} catch (NotFoundException e) {
			log.info("Unable to find an ACE for the given object", e);
		} catch (UnloadedSidException e) {
			log.error("Unloaded Sid", e);
		}

		return isGranted;
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> void removePermission(Class<T> clazz, Serializable identifier, User user, Permission permission) {
		Sid sid = new PrincipalSid(user.getUsername());
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		MutableAcl acl = (MutableAcl) readAclById(identity);		
		AccessControlEntry[] entries = acl.getEntries().toArray(new AccessControlEntry[acl.getEntries().size()]);		
		for (int i = 0; i < acl.getEntries().size(); i++) {
			if (entries[i].getSid().equals(sid) && entries[i].getPermission().equals(permission)) {
				acl.deleteAce(i);
			}
		}		
		updateAcl(acl);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> void addPermission(Class<T> clazz, Serializable identifier, Role role, Permission permission) {
		ObjectIdentity identity = new ObjectIdentityImpl(clazz, identifier);
		MutableAcl acl = isNewAcl(identity);
		isPermissionGranted(permission, new GrantedAuthoritySid(role.getName()), acl);
		updateAcl(acl);
	}


	public <T> boolean isPermissionGranted(Class<T> clazz, Serializable identifier, Role role, Permission... permissions) {
		Sid sid = new GrantedAuthoritySid(role.getName());
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		MutableAcl acl = (MutableAcl) readAclById(identity);		
		boolean isGranted = false;
		try {
			isGranted = acl.isGranted(Arrays.asList(permissions), Arrays.asList(sid), false);
		} catch (NotFoundException e) {
			log.info("Unable to find an ACE for the given object", e);
		} catch (UnloadedSidException e) {
			log.error("Unloaded Sid", e);
		}

		return isGranted;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> void removePermission(Class<T> clazz, Serializable identifier, Role role, Permission permission) {
		Sid sid = new GrantedAuthoritySid(role.getName());
		ObjectIdentity identity = new ObjectIdentityImpl(clazz.getCanonicalName(), identifier);
		MutableAcl acl = (MutableAcl) readAclById(identity);		
		AccessControlEntry[] entries = acl.getEntries().toArray(new AccessControlEntry[acl.getEntries().size()]);		
		for (int i = 0; i < acl.getEntries().size(); i++) {
			if (entries[i].getSid().equals(sid) && entries[i].getPermission().equals(permission)) {
				acl.deleteAce(i);
			}
		}		
		updateAcl(acl);
	}
	
	
	private void isPermissionGranted(Permission permission, Sid sid, MutableAcl acl) {
		try {
			acl.isGranted(Arrays.asList(permission), Arrays.asList(sid), false);
		} catch (NotFoundException e) {
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		}
	}
	
	private MutableAcl isNewAcl(ObjectIdentity identity) {
		MutableAcl acl;
		try {
			acl = (MutableAcl) readAclById(identity);
		} catch (NotFoundException e) {
			acl = createAcl(identity);
		}
		return acl;
	}

 
	public PermissionsBundle getPermissionBundle(Object object) {
		
		
		/*if(object instanceof ModelObject ) {
			ModelObject mo = (ModelObject)object;
			
			Models.valueOf(objectType)mo.getObjectType()
			
		}
		
		if( object instanceof Project) {
			return getPermissionBundle( SecurityHelper.getAuthentication(), Project.class, ((Project) object ).getProjectId() );
		}
		*/
		return new PermissionsBundle();
	}
	
    public PermissionsBundle getPermissionBundle( Authentication authentication, Class objectType , long objectId ) {    
    		
    		final PermissionsBundle bundle = new PermissionsBundle();    		
    		log.debug("permissions for {} {} - {}", objectType , objectId, authentication );
    		getFinalGrantedPermissions(authentication, objectType,  objectId, new PermissionsSetter() {
    			private boolean isGranted ( MutableAcl acl, Permission permission, List<Sid> sids ) {
    				boolean isGranted = false;
	    			try {
	    					isGranted = acl.isGranted(Arrays.asList(permission), sids, false);
					} catch (NotFoundException e) { }	
	    				return isGranted;
	    			}
    			
    			public void execute(List<Sid> sids, MutableAcl acl) {			 		
					log.debug("anonymous : {}", SecurityHelper.isAnonymous());
					if( acl != null)
					{						
				 		if( !SecurityHelper.isAnonymous() )
				 		{
				 			log.debug("is granted {} {}" , CommunityPermissions.ADMINISTRATION, sids );
				 			try {
								bundle.admin = isGranted( acl, (Permission)CommunityPermissions.ADMINISTRATION, sids);
							} catch (NotFoundException e) {
								
							}			 	
				 			log.debug("is granted {} > {}" , CommunityPermissions.ADMINISTRATION, bundle.admin );
				 		}	
				 		
				 		if( bundle.admin ) {
			    				bundle.read = true;
			    				bundle.write = true;
			    				bundle.create = true;
			    				bundle.delete = true;
			    				bundle.createThread = true;
			    				bundle.createThreadMessage = true;
			    				bundle.createAttachment = true;
			    				bundle.createImage = true;
			    				bundle.createComment = true;
			    				bundle.readComment = true;    			 					
						}else {
							bundle.read = isGranted( acl, (Permission)CommunityPermissions.READ, sids);		
							bundle.write = isGranted( acl, (Permission)CommunityPermissions.WRITE, sids);		
							bundle.create = isGranted( acl, (Permission)CommunityPermissions.CREATE, sids);		
							
							if( (bundle.write || bundle.create ) && !bundle.read) 
							{
								bundle.read = true;
							}
							bundle.delete = isGranted( acl, (Permission)CommunityPermissions.DELETE, sids);		
							bundle.createThread = isGranted( acl, (Permission)CommunityPermissions.CREATE_THREAD, sids);		
							bundle.createThreadMessage = isGranted( acl, (Permission)CommunityPermissions.CREATE_THREAD_MESSAGE, sids);		
							bundle.createAttachment = isGranted( acl, (Permission)CommunityPermissions.CREATE_ATTACHMENT, sids);		
							bundle.createImage = isGranted( acl, (Permission)CommunityPermissions.CREATE_IMAGE, sids);		
							bundle.createComment = isGranted( acl, (Permission)CommunityPermissions.CREATE_COMMENT, sids);		
							bundle.readComment = isGranted( acl, (Permission)CommunityPermissions.READ_COMMENT, sids);	
						}
					}

				}});    		
    		return bundle;
    } 
   
}
