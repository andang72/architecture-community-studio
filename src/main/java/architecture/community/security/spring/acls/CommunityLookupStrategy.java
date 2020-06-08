package architecture.community.security.spring.acls;

import javax.sql.DataSource;

import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;

public class CommunityLookupStrategy extends BasicLookupStrategy {

	public CommunityLookupStrategy(DataSource dataSource, AclCache aclCache, AclAuthorizationStrategy aclAuthorizationStrategy, AuditLogger auditLogger) {
		super(dataSource, aclCache, aclAuthorizationStrategy, auditLogger);
		setPermissionFactory(new CommunityPermissionFactory());
	}

	public CommunityLookupStrategy(DataSource dataSource, AclCache aclCache, AclAuthorizationStrategy aclAuthorizationStrategy, PermissionGrantingStrategy grantingStrategy) {
		super(dataSource, aclCache, aclAuthorizationStrategy, grantingStrategy);
		setPermissionFactory(new CommunityPermissionFactory());
	}

}
