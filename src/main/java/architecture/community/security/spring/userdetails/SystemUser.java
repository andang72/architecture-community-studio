package architecture.community.security.spring.userdetails;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class SystemUser extends org.springframework.security.core.userdetails.User implements architecture.community.user.User {

	public static final String username = "SYSTEM";
	public static final Long userId = 0x0L;
	
	public SystemUser() {
		super(username, "", true, true, true, true, getSystemAuthorities());
		// TODO Auto-generated constructor stub
	}

	protected static List<GrantedAuthority> getSystemAuthorities() {
		return AuthorityUtils.createAuthorityList("ROLE_SYSTEM");
	}

	@Override
	public Map<String, String> getProperties() {
		return new HashMap<String, String>();
	}

	@Override
	public void setProperties(Map<String, String> properties) {
	}

	@Override
	public long getUserId() { 
		return  userId;
	}

	@Override
	public String getName() { 
		return username;
	}

	@Override
	public boolean isNameVisible() { 
		return false;
	}

	@Override
	public boolean isEmailVisible() { 
		return false;
	}

	@Override
	public Status getStatus() {
		return Status.NONE;
	}

	@Override
	public String getPasswordHash() { 
		return null;
	}

	@Override
	public boolean isAnonymous() { 
		return false;
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getCreationDate() { 
		return null;
	}

	@Override
	public Date getModifiedDate() { 
		return null;
	}

	@Override
	public boolean isExternal() { 
		return false;
	}

	@Override
	public String getFirstName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastName() {
		// TODO Auto-generated method stub
		return null;
	}

}