package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class GroovyForm implements Serializable {

	private boolean exist = false;
	private long scriptId = 0L;
	private String location;
	private String filename;
	private String packageName;
	private String className;
	private String superClassName;
	private boolean setUser = false;
	private boolean setMultipart = false;

	private RequestBodyInfo requestBody;
	private List<ClassInfo> services;
	private List<ParameterInfo> parameters;
	private List<String> requiredRoles;
	private List<String> dependencies;
	
	private String content;

	public boolean isSetMultipart() {
		return setMultipart;
	}

	public void setSetMultipart(boolean setMultipart) {
		this.setMultipart = setMultipart;
	}

	public boolean isSetUser() {
		return setUser;
	}

	public void setSetUser(boolean setUser) {
		this.setUser = setUser;
	}

	public List<ParameterInfo> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParameterInfo> parameters) {
		this.parameters = parameters;
	}

	public RequestBodyInfo getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(RequestBodyInfo requestBody) {
		this.requestBody = requestBody;
	}

	public long getScriptId() {
		return scriptId;
	}

	public void setScriptId(long scriptId) {
		this.scriptId = scriptId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public List<ClassInfo> getServices() {
		return services;
	}

	public void setServices(List<ClassInfo> services) {
		this.services = services;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getRequiredRoles() {
		return requiredRoles;
	}

	public void setRequiredRoles(List<String> requiredRoles) {
		this.requiredRoles = requiredRoles;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}
	
	

	public List<String> getDependencies() {
		if( dependencies == null )
			return Collections.EMPTY_LIST;
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	} 

	public static class RequestBodyInfo implements Serializable {
		
		private boolean enabled = false;
		private String className;
		private String name;

		public RequestBodyInfo() {
			this.enabled = false;
			this.className = null;
			this.name = null;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	} 
	
	public static class ParameterInfo implements Serializable {

		private String name;

		private String defaultValue;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

	}
	
	public static class ClassInfo implements Serializable {

		private String className;
		private String name;
		private boolean required = false;
		private List<String> dependencies;
		
		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public List<String> getDependencies() {
			return dependencies;
		}

		public void setDependencies(List<String> dependencies) {
			this.dependencies = dependencies;
		} 
		
	}

}
