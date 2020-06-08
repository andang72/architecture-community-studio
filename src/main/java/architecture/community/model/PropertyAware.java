package architecture.community.model;

import java.util.Map;

public interface PropertyAware {

	public abstract Map<String, String> getProperties();

    public abstract void setProperties(Map<String, String> properties);
/*
    public abstract boolean getBooleanProperty(String name, boolean defaultValue);

    public abstract long getLongProperty(String name, long defaultValue);

    public abstract int getIntProperty(String name, int defaultValue);

    public abstract String getProperty(String name, String defaultValue);
*/
    
}
