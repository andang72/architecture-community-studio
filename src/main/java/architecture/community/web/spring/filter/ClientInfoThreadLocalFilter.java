package architecture.community.web.spring.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

@WebFilter(filterName = "clientInfoThreadLocalFilter", urlPatterns = "/*")
public class ClientInfoThreadLocalFilter extends OncePerRequestFilter {

    public static final String CONST_IP_ADDRESS_HEADER = "alternativeIpAddressHeader";
    public static final String CONST_SERVER_IP_ADDRESS_HEADER = "alternateServerAddrHeaderName";
    public static final String CONST_USE_SERVER_HOST_ADDRESS = "useServerHostAddress";

    private String alternateLocalAddrHeaderName;
    
    private boolean useServerHostAddress;
    
    private String alternateServerAddrHeaderName;
 
	protected void initFilterBean() throws ServletException { 
		logger.debug("Init ClientInfoThreadLocalFilter"); 
		final FilterConfig filterConfig = getFilterConfig();
		if(filterConfig != null ) {
			this.alternateLocalAddrHeaderName = filterConfig.getInitParameter(CONST_IP_ADDRESS_HEADER);
	        this.alternateServerAddrHeaderName = filterConfig.getInitParameter(CONST_SERVER_IP_ADDRESS_HEADER);
	        String useServerHostAddr = filterConfig.getInitParameter(CONST_USE_SERVER_HOST_ADDRESS);
	        if (useServerHostAddr != null && !useServerHostAddr.isEmpty()) {
	            this.useServerHostAddress = Boolean.valueOf(useServerHostAddr);
	        }
		}
	}
	
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			logger.debug("filter client info..");
            final ClientInfo clientInfo =
                    new ClientInfo((HttpServletRequest) request,
                            this.alternateServerAddrHeaderName,
                            this.alternateLocalAddrHeaderName,
                            this.useServerHostAddress);
            ClientInfoHolder.setClientInfo(clientInfo);
            filterChain.doFilter(request, response);
        } finally {
            ClientInfoHolder.clear();
        }
		
	}
     
}
