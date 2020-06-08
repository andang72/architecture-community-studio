package architecture.community.audit;

import java.io.Serializable;

public interface AuditPointRuntimeInfo extends Serializable {

    /**
     * @return String representation of this audit point runtime execution context
     */
    default String asString() {
        return null;
    }
}