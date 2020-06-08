package architecture.community.security.spring.acls;

import java.util.List;

import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Sid;

public interface PermissionsSetter {
		
	public abstract void execute(List<Sid> sids , MutableAcl acl);

}
