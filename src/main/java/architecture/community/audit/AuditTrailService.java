package architecture.community.audit;

import java.time.LocalDate;
import java.util.List;

public interface AuditTrailService {

    /**
     * Make an audit trail record. Implementations could use any type of back end medium to serialize audit trail
     * data i.e. RDBMS, log file, IO stream, SMTP, JMS queue or what ever else imaginable.
     * <p>
     * This concept is somewhat similar to log4j Appender.
     *
     * @param auditActionContext the audit action context
     */
    void record(AuditActionContext auditActionContext);

    /**
     * Gets audit records since.
     *
     * @param sinceDate the since date
     * @return the audit records since
     */
    List<? extends AuditActionContext> getAuditRecordsSince(LocalDate sinceDate); 
    
    /**
     * Remove all.
     */
    void removeAll();
    
    void updateAll ();
}
