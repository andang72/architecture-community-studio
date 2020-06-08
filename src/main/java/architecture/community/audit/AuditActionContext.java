package architecture.community.audit;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.spring.filter.ClientInfo;
import architecture.community.web.spring.filter.ClientInfoHolder;

/**
 * Immutable container holding the core elements of an audit-able action that need to be recorded
 * as an audit trail record.
 * 
 * The WHO: who performed an action being audited.
 * The WHAT: what system resource being targeted by this audited action
 * The ACTION: what audited action is being performed
 * The APPLICATION_CODE: an arbitrary string token identifying application running an audited action
 * The WHEN: a timestamp of an audited action
 * The CLIENT_IP: an IP address of the client invoking an audited action
 * The SERVER_IP: an IP address of the server running an audited action
 * 
 * @author donghyuck
 * 
 */
public class AuditActionContext implements Serializable {
 
    /**
	 * Unique Id for serialization.
	 */
	private static final long serialVersionUID = 8733506140194792650L;

	/**
     * This is <i>WHO</i>
     */
    @JsonProperty
    private final String principal;

    /**
     * This is <i>WHAT</i>
     */
    @JsonProperty
    private final String resourceOperatedUpon;

    /**
     * This is <i>ACTION</i>
     */
    @JsonProperty
    private final String actionPerformed;

    /**
     * This is <i>Application from which operation has been performed</i>
     */
    @JsonProperty
    private final String applicationCode;

    /**
     * This is <i>WHEN</i>
     */
    @JsonProperty
    private final Date whenActionWasPerformed;

    /**
     * Client IP Address
     */
    @JsonProperty
    private final String clientIpAddress;

    /**
     * Server IP Address
     */
    @JsonProperty
    private final String serverIpAddress;

    @JsonProperty
    private final String clientUserAgent;
    
    private AuditActionContext() {
    	this.principal = null;
        this.resourceOperatedUpon = null;
        this.actionPerformed = null;
        this.applicationCode = null;
        this.whenActionWasPerformed = null;
        this.clientIpAddress = null;
        this.serverIpAddress = null;
        this.clientUserAgent = null;
    }
    
    @JsonCreator
    public AuditActionContext(@JsonProperty("principal") final String principal,
        @JsonProperty("resourceOperatedUpon") final String resourceOperatedUpon,
        @JsonProperty("actionPerformed") final String actionPerformed,
        @JsonProperty("applicationCode") final String applicationCode,
        @JsonProperty("whenActionWasPerformed") final Date whenActionWasPerformed,
        @JsonProperty("clientIpAddress") final String clientIpAddress,
        @JsonProperty("serverIpAddress") final String serverIpAddress,
        @JsonProperty("clientUserAgent") final String clientUserAgent) {

        this.principal = principal;
        this.resourceOperatedUpon = resourceOperatedUpon;
        this.actionPerformed = actionPerformed;
        this.applicationCode = applicationCode;
        this.whenActionWasPerformed = new Date(whenActionWasPerformed.getTime());
        this.clientIpAddress = clientIpAddress;
        this.serverIpAddress = serverIpAddress;
        this.clientUserAgent = clientUserAgent;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getResourceOperatedUpon() {
        return resourceOperatedUpon;
    }

    public String getActionPerformed() {
        return actionPerformed;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public Date getWhenActionWasPerformed() {
        return new Date(whenActionWasPerformed.getTime());
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    public String getServerIpAddress() {
        return this.serverIpAddress;
    }
     
    
    public String getClientUserAgent() {
		return clientUserAgent;
	}



	public static class Builder {
    	
    	AuditActionContext context = new AuditActionContext();
    	
    	public Builder(HttpServletRequest request, HttpServletResponse response) {
			final ClientInfo info = new ClientInfo(request);
			context = new AuditActionContext(
				null,
				null, 
				null, 
				null, 
				new Date(), 
				info.getClientIpAddress(), 
				info.getServerIpAddress(), 
				info.getUserAgent());
		}
    	
    	public Builder(HttpServletRequest request, HttpServletResponse response, String resource, String action, String code) {
			final ClientInfo info = new ClientInfo(request);
			final User user = SecurityHelper.getUser();
			context = new AuditActionContext(
				user.getUsername(),
				resource, 
				action, 
				code, 
				new Date(), 
				info.getClientIpAddress(), 
				info.getServerIpAddress(), 
				info.getUserAgent());
		}

    	public Builder(String resource, String action, String code) {
    		final ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
    		final User user = SecurityHelper.getUser();
    		context = new AuditActionContext(
    			user.getUsername(),
    			resource, 
    			action, 
    			code, 
    			new Date(), 
    			clientInfo != null ? clientInfo.getClientIpAddress() : null, 
    			clientInfo != null ? clientInfo.getServerIpAddress(): null, 
    			clientInfo != null ? clientInfo.getUserAgent():null);
		}
    	
    	public AuditActionContext build() {
    		return context;
    	}
    	
    }
}
