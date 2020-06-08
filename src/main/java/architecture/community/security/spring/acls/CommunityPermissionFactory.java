package architecture.community.security.spring.acls;

import java.util.Map;

import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.model.Permission;

public class CommunityPermissionFactory extends DefaultPermissionFactory {

	public CommunityPermissionFactory() { 
		registerPublicPermissions(CommunityPermissions.class);
	}

	public CommunityPermissionFactory(Class<? extends Permission> permissionClass) {
		super(permissionClass); 
	}

	public CommunityPermissionFactory(Map<String, ? extends Permission> namedPermissions) {
		super(namedPermissions); 
	}

}
