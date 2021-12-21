package architecture.community.services;

import java.util.Map;

import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack;

public interface HttpClientService {

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"; 
	
	public <T> T get (String serviceUrl, Map<String, String> headers, Map<String, String> values, ResponseCallBack<T> handler) throws Exception ;
	
	public <T> T get (String serviceUrl, Map<String, String> values, ResponseCallBack<T> handler) throws Exception ;
	
	public <T> T post (String serviceUrl, Map<String, String> values, ResponseCallBack<T> handler) throws Exception;
}
