package architecture.community.model;

import java.util.Map;

public interface PropertyModelObjectAware extends ModelObject {

	public abstract Map<String, String> getProperties();

    public abstract void setProperties(Map<String, String> properties);
    
}
