package architecture.community.audit.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

import architecture.community.audit.AuditActionContext;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;

public class AuditLogEvent extends ApplicationEvent  {

	public static final String CREATE = "create";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	public static final String READ = "read";
	
	private AuditActionContext context;
	private Integer objectType ;
	private Long objectId;
	private User actor ;
	
	public AuditLogEvent(Object source) {
		super(source); 
	}

	public User getActor() {
		return actor;
	}

	public void setActor(User actor) {
		this.actor = actor;
	}

	public AuditActionContext getContext() {
		return context;
	}

	public void setContext(AuditActionContext context) {
		this.context = context;
	}

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public static class Builder {
		
		private AuditLogEvent event ;
		private HttpServletRequest request;
		private HttpServletResponse response;
		private String resource ;
		private String action;
		private String code ;
		private double totalTimeSeconds;
		
		public Builder (Object source) {
			this.request = null;
			this.response = null;
			this.event = new AuditLogEvent(source);
			this.event.actor = SecurityHelper.getUser();
			this.action = null;
			this.code = null;
			this.resource = null;
		}
		
		public Builder (HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
			this.event = new AuditLogEvent(null);
			this.event.actor = SecurityHelper.getUser();
			this.action = null;
			this.code = null;
			this.resource = null;
		}
		
		
		public Builder (HttpServletRequest request, HttpServletResponse response, Object source) {
			this.request = request;
			this.response = response;
			this.event = new AuditLogEvent(source);
			this.event.actor = SecurityHelper.getUser();
			this.action = null;
			this.code = null;
			this.resource = null;
		}
		
		public Builder(HttpServletRequest request, HttpServletResponse response, Object source, String resource, String action, String code) {
			this.event = new AuditLogEvent(source);
			this.event.actor = SecurityHelper.getUser();
			this.event.context = (new AuditActionContext.Builder(request, response)).build();
		}
		
		public AuditLogEvent build() {
			
			if( this.request == null ) {
				this.event.context = (new AuditActionContext.Builder(resource, action, code )).build();
			}else {
				this.event.context = (new AuditActionContext.Builder(request, response, resource, action, code )).build();
			}
			
			return event;
		}
		
		public Builder resource (String resource) {
			this.resource = resource;
			return this;
		}
		
		public Builder action (String action) {
			this.action = action;
			return this;
		}
		
		public Builder code (String code) {
			this.code = code;
			return this;
		}
		
		public Builder objectTypeAndObjectId (int objectType, long objectId) {
			event.objectType = objectType;
			event.objectId = objectId;
			return this;
		}
		
		public Builder objectId (long objectId) {
			event.objectId = objectId;
			return this;
		}
		
		public Builder objectType (int objectType) {
			event.objectType = objectType;
			return this;
		}
		
		public Builder totalTimeSeconds(double totalTimeSeconds) {
			this.totalTimeSeconds = totalTimeSeconds;
			return this;
		}
	}
}
