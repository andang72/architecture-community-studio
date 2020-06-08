package architecture.community.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import architecture.ee.util.StringUtils;

public class PropertyAwareSupport {

	private Map<String, String> properties = null;

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<String, String> getProperties() {
		synchronized (this) {
			if (properties == null) {
				properties = new ConcurrentHashMap<String, String>();
			}
		}
		return properties;
	}

	public boolean getBooleanProperty(String name, boolean defaultValue) {
		String value = getProperties().get(name);
		String valueToUse = StringUtils.defaultString(value, Boolean.toString(defaultValue));
		return Boolean.parseBoolean(valueToUse);
	}

	public long getLongProperty(String name, long defaultValue) {
		String value = getProperties().get(name);
		String valueToUse = StringUtils.defaultString(value, Long.toString(defaultValue));
		return Long.parseLong(valueToUse);
	}

	public int getIntProperty(String name, int defaultValue) {
		String value = getProperties().get(name);
		String valueToUse = StringUtils.defaultString(value, Integer.toString(defaultValue));
		return Integer.parseInt(valueToUse);
	}

	public String getProperty(String name, String defaultString) {
		String value = getProperties().get(name);
		return StringUtils.defaultString(value, defaultString);
	}

}
