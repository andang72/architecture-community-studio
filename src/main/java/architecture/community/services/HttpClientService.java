package architecture.community.services;

import java.util.Map;

import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack;

public interface HttpClientService {

	public <T> T get (String serviceUrl, Map<String, String> values, ResponseCallBack<T> handler) throws Exception ;
	
	public <T> T post (String serviceUrl, Map<String, String> values, ResponseCallBack<T> handler) throws Exception;
}
