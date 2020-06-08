package architecture.community.web.spring.filter;

import java.net.Inet4Address;

import javax.servlet.http.HttpServletRequest;

public class ClientInfo {
    public static ClientInfo EMPTY_CLIENT_INFO = new ClientInfo();

    /** IP Address of the server (local). */
    private final String serverIpAddress;

    /** IP Address of the client (Remote) */
    private final String clientIpAddress;

    private final String geoLocation;

    private final String userAgent;
    
    private ClientInfo() {
        this(null);
    }

    public ClientInfo(final HttpServletRequest request) {
        this(request, null, null, false);
    }
    
    public ClientInfo(final HttpServletRequest request,
                      final String alternateServerAddrHeaderName,
                      final String alternateLocalAddrHeaderName,
                      final boolean useServerHostAddress) {

        try {
            String serverIpAddress = request != null ? request.getLocalAddr() : null;
            String clientIpAddress = request != null ? request.getRemoteAddr() : null; 
            if (request == null) {
                this.geoLocation = "unknown";
                this.userAgent = "unknown";
            } else {
                if (useServerHostAddress) {
                    serverIpAddress = Inet4Address.getLocalHost().getHostAddress();
                } else if (alternateServerAddrHeaderName != null && !alternateServerAddrHeaderName.isEmpty()) {
                    serverIpAddress = request.getHeader(alternateServerAddrHeaderName) != null  ? request.getHeader(alternateServerAddrHeaderName) : request.getLocalAddr();
                }

                if (alternateLocalAddrHeaderName != null && !alternateLocalAddrHeaderName.isEmpty()) {
                    clientIpAddress = request.getHeader(alternateLocalAddrHeaderName) != null ? request.getHeader  (alternateLocalAddrHeaderName) : request.getRemoteAddr();
                }
                
                String header = request.getHeader("user-agent");
                this.userAgent = header == null ? "unknown" : header;
                
                String geo = request.getParameter("geolocation");
                if (geo == null) {
                    geo = request.getHeader("geolocation");
                }
                this.geoLocation = geo == null ? "unknown" : geo;
            } 
            this.serverIpAddress = serverIpAddress == null ? "unknown" : serverIpAddress;
            this.clientIpAddress = clientIpAddress == null ? "unknown" : clientIpAddress;
            
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerIpAddress() {
        return this.serverIpAddress;
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
