package architecture.community.security.spring.acls;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.ObjectIdentityRetrievalStrategyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityGenerator;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
/**
 * custom permission evaluator.
 * 
 */
public class CommunityAclPermissionEvaluator implements PermissionEvaluator {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final AclService aclService;
	private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = new ObjectIdentityRetrievalStrategyImpl();
	private ObjectIdentityGenerator objectIdentityGenerator = new ObjectIdentityRetrievalStrategyImpl();
	private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
	private PermissionFactory permissionFactory = new CommunityPermissionFactory(); 

	public CommunityAclPermissionEvaluator(AclService aclService) {
		this.aclService = aclService;
	}	

	/**
	 * Determines whether the user has the given permission(s) on the domain object using
	 * the ACL configuration. If the domain object is null, returns false (this can always
	 * be overridden using a null check in the expression itself).
	 */
	public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
		if (domainObject == null) {
			return false;
		} 
		ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy.getObjectIdentity(domainObject); 
		return checkPermission(authentication, objectIdentity, permission);
	}

	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		ObjectIdentity objectIdentity = objectIdentityGenerator.createObjectIdentity( targetId, targetType);	
		return checkPermission(authentication, objectIdentity, permission);
	}

	private boolean checkPermission(Authentication authentication, ObjectIdentity oid, Object permission) {
		// Obtain the SIDs applicable to the principal
		List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
		List<Permission> requiredPermission = resolvePermission(permission);

		final boolean debug = logger.isDebugEnabled(); 
		if (debug) {
			logger.debug("Sids:{}, Perms:{}" , sids , requiredPermission );	
			logger.debug("Checking permission <{}}> for object <{}}>" , permission, oid);
		}

		try {
			// Lookup only ACLs for SIDs we're interested in
			if( aclService instanceof JdbcCommunityAclService ) {
				boolean isAdmin = ((JdbcCommunityAclService)aclService).isPermissionGrantedFinally(authentication, oid, requiredPermission);
				if( isAdmin )
					return true;
			}
			Acl acl = aclService.readAclById(oid, sids);
			if (acl.isGranted(requiredPermission, sids, false)) {
				if (debug) {
					logger.debug("Access is granted");
				}
				return true;
			}		

			if (debug) {
				logger.debug("Returning false - ACLs returned, but insufficient permissions for this principal");
			}
		}
		catch (NotFoundException nfe) {
			if (debug) {
				logger.debug("Returning false - no ACLs apply for this principal");
			}
		}

		return false;

	}

	List<Permission> resolvePermission(Object permission) {
		if (permission instanceof Integer) {
			return Arrays.asList(permissionFactory.buildFromMask(((Integer) permission).intValue()));
		}

		if (permission instanceof Permission) {
			return Arrays.asList((Permission) permission);
		}

		if (permission instanceof Permission[]) {
			return Arrays.asList((Permission[]) permission);
		}

		if (permission instanceof String) {
			String permString = (String) permission;
			Permission p;

			try {
				p = permissionFactory.buildFromName(permString);
			}
			catch (IllegalArgumentException notfound) {
				p = permissionFactory.buildFromName(permString.toUpperCase(Locale.ENGLISH));
			}

			if (p != null) {
				return Arrays.asList(p);
			}

		}
		throw new IllegalArgumentException("Unsupported permission: " + permission);
	}

	public void setObjectIdentityRetrievalStrategy(
			ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy) {
		this.objectIdentityRetrievalStrategy = objectIdentityRetrievalStrategy;
	}

	public void setObjectIdentityGenerator(ObjectIdentityGenerator objectIdentityGenerator) {
		this.objectIdentityGenerator = objectIdentityGenerator;
	}

	public void setSidRetrievalStrategy(SidRetrievalStrategy sidRetrievalStrategy) {
		this.sidRetrievalStrategy = sidRetrievalStrategy;
	}

	public void setPermissionFactory(PermissionFactory permissionFactory) {
		this.permissionFactory = permissionFactory;
	}
}
