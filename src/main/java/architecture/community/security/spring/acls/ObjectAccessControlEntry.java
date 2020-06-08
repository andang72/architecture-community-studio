package architecture.community.security.spring.acls;
import java.io.Serializable;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;

public class ObjectAccessControlEntry implements Serializable {

	private long id;
	
	private boolean granting;
	
	private String permission;
	
	private String grantedAuthorityOwner;
	
	private String grantedAuthority;
	
	private String domainObjectClass;
	
	private long domainObjectId;
		
	public ObjectAccessControlEntry() {
		id = 0L;
		granting = false;
		permission = null;
		grantedAuthorityOwner = null;
		grantedAuthority = null;
		domainObjectClass = null;
		domainObjectId = 0L;
	}
	
	public ObjectAccessControlEntry(org.springframework.security.acls.model.AccessControlEntry entry) {
		
		id = Long.parseLong(entry.getId().toString());
		granting = entry.isGranting();
		permission = CommunityPermissions.getPermissionByMask(entry.getPermission().getMask()).getName();
		domainObjectClass = entry.getAcl().getObjectIdentity().getType();
		domainObjectId = Long.parseLong( entry.getAcl().getObjectIdentity().getIdentifier().toString() );		
		if( entry.getSid() instanceof GrantedAuthoritySid ) {
			grantedAuthority = "ROLE";
			grantedAuthorityOwner = ((GrantedAuthoritySid)entry.getSid()).getGrantedAuthority();
		}else if ( entry.getSid() instanceof PrincipalSid) {
			grantedAuthority = "USER";
			grantedAuthorityOwner = ((PrincipalSid)entry.getSid()).getPrincipal();
		}		
	}
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isGranting() {
		return granting;
	}

	public void setGranting(boolean granting) {
		this.granting = granting;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getGrantedAuthorityOwner() {
		return grantedAuthorityOwner;
	}

	public void setGrantedAuthorityOwner(String grantedAuthorityOwner) {
		this.grantedAuthorityOwner = grantedAuthorityOwner;
	}

	public String getGrantedAuthority() {
		return grantedAuthority;
	}

	public void setGrantedAuthority(String grantedAuthority) {
		this.grantedAuthority = grantedAuthority;
	}

	public String getDomainObjectClass() {
		return domainObjectClass;
	}

	public void setDomainObjectClass(String domainObjectClass) {
		this.domainObjectClass = domainObjectClass;
	}

	public long getDomainObjectId() {
		return domainObjectId;
	}

	public void setDomainObjectId(long domainObjectId) {
		this.domainObjectId = domainObjectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObjectAccessControlEntry [id=").append(id).append(", granting=").append(granting).append(", ");
		if (permission != null)
			builder.append("permission=").append(permission).append(", ");
		if (grantedAuthorityOwner != null)
			builder.append("grantedAuthorityOwner=").append(grantedAuthorityOwner).append(", ");
		if (grantedAuthority != null)
			builder.append("grantedAuthority=").append(grantedAuthority).append(", ");
		if (domainObjectClass != null)
			builder.append("domainObjectClass=").append(domainObjectClass).append(", ");
		builder.append("domainObjectId=").append(domainObjectId).append("]");
		return builder.toString();
	}

	
}
