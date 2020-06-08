package architecture.community.services.mail;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MailServicesConfig implements Serializable {
	
	private String username;
	private String password;
	private String host;
	private Integer port;
	private String protocol;
	private Boolean ssl;
	private String defaultEncoding;
	private Boolean enabled;  
	private Map<String, String> properties;
	private String beanName;
	
	
	
	public MailServicesConfig() { 
		this.properties = new HashMap<String, String>();
	} 
	
	public String getBeanName() {
		return beanName;
	} 
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public Boolean getSsl() {
		return ssl;
	}
	public void setSsl(Boolean ssl) {
		this.ssl = ssl;
	}
	public String getDefaultEncoding() {
		return defaultEncoding;
	}
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("MailServicesConfig [");
		if (enabled != null)
			builder.append("enabled=").append(enabled).append(", ");
		if (username != null)
			builder.append("username=").append(username).append(", ");
		if (password != null)
			builder.append("password=").append(password).append(", ");
		if (host != null)
			builder.append("host=").append(host).append(", ");
		if (port != null)
			builder.append("port=").append(port).append(", ");
		if (protocol != null)
			builder.append("protocol=").append(protocol).append(", ");
		if (ssl != null)
			builder.append("ssl=").append(ssl).append(", ");
		if (defaultEncoding != null)
			builder.append("defaultEncoding=").append(defaultEncoding).append(", ");
		if (properties != null)
			builder.append("properties=").append(toString(properties.entrySet(), maxLen));
		builder.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
}
