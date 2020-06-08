package architecture.community.user;

import java.util.Date;

import architecture.community.model.PropertyAware;

public interface Company extends PropertyAware {

	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public abstract Long getCompanyId();

    public abstract void setCompanyId(Long companyId);

    public abstract void setDescription( String name);
    
    public abstract String getDescription();
    
    public abstract String getDisplayName();

    public abstract void setDisplayName(String displayName);

    public abstract String getDomain();

    public abstract void setDomain(String domain);
    
	public abstract Date getCreationDate();
	
	public abstract Date getModifiedDate();
	
	public abstract void setCreationDate(Date date);
	
	public abstract void setModifiedDate(Date date);
	
}
