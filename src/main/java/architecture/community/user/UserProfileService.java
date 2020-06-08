package architecture.community.user;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.exception.NotFoundException;

public interface UserProfileService {
 
	public boolean isEnabled();
	
	public boolean isCacheable() ;
	
	public void refresh ();
	
	public UserProfile  getUserProfile( User user ) throws NotFoundException;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate ( User user , UserProfile profile);
	
}
