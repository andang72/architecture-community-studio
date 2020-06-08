package architecture.community.audit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.community.audit.annotation.Audit;
import architecture.community.audit.annotation.Audits;
import architecture.community.audit.spi.AuditActionResolver;
import architecture.community.audit.spi.AuditResourceResolver;
import architecture.community.audit.spi.ClientInfoResolver;
import architecture.community.audit.spi.DefaultClientInfoResolver;
import architecture.community.audit.spi.PrincipalResolver;
import architecture.community.web.spring.filter.ClientInfo;


@Aspect
public class AuditTrailManagementAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailManagementAspect.class);

    private final PrincipalResolver auditPrincipalResolver;

    private final Map<String, AuditActionResolver> auditActionResolvers;

    private final Map<String, AuditResourceResolver> auditResourceResolvers;

    private final List<AuditTrailService> auditTrailManagers;

    private final String applicationCode;

    private ClientInfoResolver clientInfoResolver = new DefaultClientInfoResolver();

    private boolean failOnAuditFailures = true;

    /**
     * Constructs an AuditTrailManagementAspect with the following parameters.  Also, registers some default AuditActionResolvers including the
     * {@link DefaultAuditActionResolver}, the {@link BooleanAuditActionResolver} and the {@link ObjectCreationAuditActionResolver}.
     *
     * @param applicationCode            the overall code that identifies this application.
     * @param auditablePrincipalResolver the resolver which will locate principals.
     * @param auditTrailManagers         the list of managers to write the audit trail out to.
     * @param auditActionResolverMap     the map of resolvers by name provided in the annotation on the method.
     * @param auditResourceResolverMap   the map of resolvers by the name provided in the annotation on the method.
     */
    public AuditTrailManagementAspect(final String applicationCode, final PrincipalResolver auditablePrincipalResolver, final List<AuditTrailService> auditTrailManagers, final Map<String, AuditActionResolver> auditActionResolverMap, final Map<String, AuditResourceResolver> auditResourceResolverMap) {
        this.auditPrincipalResolver = auditablePrincipalResolver;
        this.auditTrailManagers = auditTrailManagers;
        this.applicationCode = applicationCode;
        this.auditActionResolvers = auditActionResolverMap;
        this.auditResourceResolvers = auditResourceResolverMap;

    }

    @Around(value = "@annotation(audits)", argNames = "audits")
    public Object handleAuditTrail(final ProceedingJoinPoint joinPoint, final Audits audits) throws Throwable {
        Object retVal = null;
        String currentPrincipal = null;
        final String[] actions = new String[audits.value().length];
        final String[][] auditableResources = new String[audits.value().length][];
        try {
            retVal = joinPoint.proceed();
            currentPrincipal = this.auditPrincipalResolver.resolveFrom(joinPoint, retVal);

            if (currentPrincipal != null) {
                for (int i = 0; i < audits.value().length; i++) {
                    final AuditActionResolver auditActionResolver = this.auditActionResolvers.get(audits.value()[i].actionResolverName());
                    final AuditResourceResolver auditResourceResolver = this.auditResourceResolvers.get(audits.value()[i].resourceResolverName());
                    auditableResources[i] = auditResourceResolver.resolveFrom(joinPoint, retVal);
                    actions[i] = auditActionResolver.resolveFrom(joinPoint, retVal, audits.value()[i]);
                }
            }
            return retVal;
        } catch (final Throwable t) {
            final Exception e = wrapIfNecessary(t);
            currentPrincipal = this.auditPrincipalResolver.resolveFrom(joinPoint, e);

            if (currentPrincipal != null) {
                for (int i = 0; i < audits.value().length; i++) {
                    auditableResources[i] = this.auditResourceResolvers.get(audits.value()[i].resourceResolverName()).resolveFrom(joinPoint, e);
                    actions[i] = auditActionResolvers.get(audits.value()[i].actionResolverName()).resolveFrom(joinPoint, e, audits.value()[i]);
                }
            }
            throw t;
        } finally {
            for (int i = 0; i < audits.value().length; i++) {
                executeAuditCode(currentPrincipal, auditableResources[i], joinPoint, retVal, actions[i], audits.value()[i]);
            }
        }
    }

    @Around(value = "@annotation(audit)", argNames = "audit")
    public Object handleAuditTrail(final ProceedingJoinPoint joinPoint, final Audit audit) throws Throwable {
        final AuditActionResolver auditActionResolver = this.auditActionResolvers.get(audit.actionResolverName());
        final AuditResourceResolver auditResourceResolver = this.auditResourceResolvers.get(audit.resourceResolverName());

        String currentPrincipal = null;
        String[] auditResource = new String[]{null};
        String action = null;
        Object retVal = null;
        try {
            retVal = joinPoint.proceed();

            currentPrincipal = this.auditPrincipalResolver.resolveFrom(joinPoint, retVal);
            auditResource = auditResourceResolver.resolveFrom(joinPoint, retVal);
            action = auditActionResolver.resolveFrom(joinPoint, retVal, audit);

            return retVal;
        } catch (final Throwable t) {
            final Exception e = wrapIfNecessary(t);
            currentPrincipal = this.auditPrincipalResolver.resolveFrom(joinPoint, e);
            auditResource = auditResourceResolver.resolveFrom(joinPoint, e);
            action = auditActionResolver.resolveFrom(joinPoint, e, audit);
            throw t;
        } finally {
            executeAuditCode(currentPrincipal, auditResource, joinPoint, retVal, action, audit);
        }
    }

    private void executeAuditCode(final String currentPrincipal, final String[] auditableResources, final ProceedingJoinPoint joinPoint, final Object retVal, final String action, final Audit audit) {
        final String applicationCode = (audit.applicationCode() != null && audit.applicationCode().length() > 0) ? audit.applicationCode() : this.applicationCode;
        final ClientInfo clientInfo = this.clientInfoResolver.resolveFrom(joinPoint, retVal);
        final Date actionDate = new Date();
        final AuditPointRuntimeInfo runtimeInfo = new AspectJAuditPointRuntimeInfo(joinPoint);

        assertNotNull(currentPrincipal, "'principal' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(action, "'actionPerformed' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(applicationCode, "'applicationCode' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(actionDate, "'whenActionPerformed' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(clientInfo.getClientIpAddress(), "'clientIpAddress' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
        assertNotNull(clientInfo.getServerIpAddress(), "'serverIpAddress' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));

        for (final String auditableResource : auditableResources) {
            assertNotNull(auditableResource, "'resourceOperatedUpon' cannot be null.\n" + getDiagnosticInfo(runtimeInfo));
            final AuditActionContext auditContext =
                new AuditActionContext(currentPrincipal, auditableResource, action, applicationCode,
                    actionDate, clientInfo.getClientIpAddress(), clientInfo.getServerIpAddress(), clientInfo.getUserAgent());

            try {
                for (final AuditTrailService manager : auditTrailManagers) {
                    manager.record(auditContext);
                }
            } catch (final Throwable e) {
                if (this.failOnAuditFailures) {
                    throw e;
                }
                LOG.error("Failed to record audit context for "
                    + auditContext.getActionPerformed()
                    + " and principal " + auditContext.getPrincipal(), e);
            }
        }
    }

    public void setFailOnAuditFailures(final boolean failOnAuditFailures) {
        this.failOnAuditFailures = failOnAuditFailures;
    }

    public void setClientInfoResolver(final ClientInfoResolver factory) {
        this.clientInfoResolver = factory;
    }

    private String getDiagnosticInfo(AuditPointRuntimeInfo runtimeInfo) {
        return "Check the correctness of @Audit annotation at the following audit point: " + runtimeInfo.asString();
    }

    private void assertNotNull(final Object o, final String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private Exception wrapIfNecessary(final Throwable t) {
        return t instanceof Exception ? (Exception) t : new Exception(t);
    }
}