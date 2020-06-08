package architecture.community.audit.spi;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.community.web.spring.filter.ClientInfo;
import architecture.community.web.spring.filter.ClientInfoHolder;

public class DefaultClientInfoResolver implements ClientInfoResolver {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public ClientInfo resolveFrom(final JoinPoint joinPoint, final Object retVal) {
        final ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
        if (clientInfo != null) {
            return clientInfo;
        }
        log.warn("No ClientInfo could be found.  Returning empty ClientInfo object.");
        return ClientInfo.EMPTY_CLIENT_INFO;
    }
}
