package architecture.community.audit;

import org.aspectj.lang.JoinPoint;

public class AspectJAuditPointRuntimeInfo  implements AuditPointRuntimeInfo {

    private JoinPoint currentJoinPoint;

    public AspectJAuditPointRuntimeInfo(JoinPoint currentJoinPoint) {
        this.currentJoinPoint = currentJoinPoint;
    }

    @Override
    public String asString() {
        return this.currentJoinPoint.toLongString();
    }
}