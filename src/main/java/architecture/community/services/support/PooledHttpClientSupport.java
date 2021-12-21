package architecture.community.services.support;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PooledHttpClientSupport {

	public static final String DEFAULT_CHARSET = "UTF-8";

	public static final String DEFAULT_CONTENT_TYPE = "text/xml; charset=utf-8"; 
	
	protected Logger log = LoggerFactory.getLogger(getClass().getName());

	private HttpClient client;

	protected HttpClient getHttpClient() {
		if (client == null) {
			client = createHttpClient();
		}
		return client;
	}
	
	public <T> T get (String serviceUrl, Map<String, String> headers, Map<String, String> values, ResponseCallBack<T> handler) throws Exception { 
		
		HttpUriRequest requestToUse;
		HttpClient client = getHttpClient(); // 송신 클라이언트 생성 				
		URIBuilder builder = new URIBuilder(serviceUrl);
		
		if( values != null ) {
			for(NameValuePair nameValuePair : getParameters(serviceUrl, values)) {
				builder.setParameter(nameValuePair.getName(), nameValuePair.getValue());
			}
			HttpGet method = createGetMethod(builder.build()); 
			requestToUse = method;
			
		}else {
			HttpGet method = createGetMethod(serviceUrl); // 송신 메소드 선언	
			requestToUse = method;
		}
		
		if( headers != null ) {
			headers.forEach((k, v) -> {
				requestToUse.setHeader(k, v); 
		    });
		} 
		
		HttpResponse response = client.execute(requestToUse);

		// Read the response
		String responseString = "";
		int statusCode = response.getStatusLine().getStatusCode();
		String message = response.getStatusLine().getReasonPhrase();
		log.debug("statusCode: " + statusCode);
		log.debug("message: " + message);
		HttpEntity responseHttpEntity = response.getEntity();
		InputStream content = responseHttpEntity.getContent();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(content, "UTF-8"));
		responseString = IOUtils.toString(buffer);
		EntityUtils.consume(responseHttpEntity);
		return handler.process(statusCode, responseString);
	}
	
	public <T> T get (String serviceUrl, Map<String, String> values, ResponseCallBack<T> handler) throws Exception { 	
		return get(serviceUrl, null, values, handler);
	}

	public <T> T post (String serviceUrl, Map<String, String> values, ResponseCallBack<T> handler) throws Exception { 
		
		HttpClient client = getHttpClient(); // 송신 클라이언트 생성 		
		HttpPost method = createPostMethod(serviceUrl); // 송신 메소드 선언
		method.setEntity(new UrlEncodedFormEntity(getParameters(serviceUrl, values), DEFAULT_CHARSET));
		HttpResponse response = client.execute(method);

		// Read the response
		String responseString = "";
		int statusCode = response.getStatusLine().getStatusCode();
		String message = response.getStatusLine().getReasonPhrase();
		log.debug("statusCode: " + statusCode);
		log.debug("message: " + message);
		HttpEntity responseHttpEntity = response.getEntity();
		InputStream content = responseHttpEntity.getContent();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		responseString = IOUtils.toString(buffer);
		EntityUtils.consume(responseHttpEntity);
		return handler.process(statusCode, responseString);
	}

	protected void parseElementAsMap(Element ele, List<String> children, List<Map<String, Object>> holder) {
		Map<String, Object> row = new HashMap<String, Object>();
		List<Element> attrs = ele.elements();
		for (Element attr : attrs) {
			String name = attr.getName();
			Object value;
			if (children.contains(name)) {
				List<Map<String, Object>> holder2 = new ArrayList<Map<String, Object>>();
				parseElementAsMap(attr, children, holder2);
				value = holder2;
			} else {
				value = StringUtils.defaultString(attr.getTextTrim(), "");
			}
			row.put(name, value);
		}
		holder.add(row);
	}

	protected List<NameValuePair> getParameters(String serviceUrl, Map<String, String> values) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

		for (String key : values.keySet()) {
			parameters.add(new BasicNameValuePair(key, values.get(key)));
		}
		return parameters;
	}

	protected List<NameValuePair> getParameters(Map<String, String> values) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

		for (String key : values.keySet()) {
			parameters.add(new BasicNameValuePair(key, values.get(key)));
		}
		return parameters;
	}

	protected HttpClient createHttpClient() {
		// 소켓 튜닝
		SocketConfig sc = SocketConfig.custom().setSoTimeout(2000).setSoKeepAlive(true).setTcpNoDelay(true).setSoReuseAddress(true).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(10);
		cm.setDefaultMaxPerRoute(50);
		return HttpClients.custom().setDefaultSocketConfig(sc).setConnectionManager(cm).build();
	}

	protected HttpGet createGetMethod(URI serviceUrl) {	
		HttpGet method = new HttpGet(serviceUrl);
		return method;
	}
	
	protected HttpGet createGetMethod(String serviceUrl) {
		HttpGet method = new HttpGet(serviceUrl);
		return method;
	}

	protected HttpPost createPostMethod(String serviceUrl) {
		HttpPost method = new HttpPost(serviceUrl);
		return method;
	}

	public interface ResponseCallBack<T> {
		T process(int statusCode, String responseString);
	}
}
