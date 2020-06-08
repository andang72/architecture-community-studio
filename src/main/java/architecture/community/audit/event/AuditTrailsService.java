package architecture.community.audit.event;

public interface AuditTrailsService {
	
	public boolean isEnabled();
	
	public abstract void leave(AuditLogEvent trails); 
	
}
