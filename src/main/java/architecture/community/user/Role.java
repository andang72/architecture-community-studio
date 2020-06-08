package architecture.community.user;

import java.util.Date;

public interface Role {
	
	public static final int MODLE_TYPE = 3;

	public abstract long getRoleId();
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	public Date getCreationDate();
	
	public Date getModifiedDate();
	
}
