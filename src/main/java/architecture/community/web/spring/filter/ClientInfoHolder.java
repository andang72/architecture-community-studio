package architecture.community.web.spring.filter;

public class ClientInfoHolder {

    private static final ThreadLocal<ClientInfo> clientInfoHolder = new InheritableThreadLocal<ClientInfo>();

    public static void setClientInfo(final ClientInfo clientInfo) {
        clientInfoHolder.set(clientInfo);
    }

    public static ClientInfo getClientInfo() {
        return clientInfoHolder.get();
    }

    public static void clear() {
        clientInfoHolder.remove();
    }
}
