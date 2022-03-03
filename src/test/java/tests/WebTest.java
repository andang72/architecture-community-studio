package tests;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.CollectionUtils;

import architecture.community.services.HttpClientService;
import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack; 

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = {
		"classpath:test-utils-context.xml" })
public class WebTest {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("communityHttpClientService")
	private HttpClientService communityHttpClientService;
	 
	@Test
	public void testCache () throws Exception {
		 Map<String, String> header = new HashMap<String, String>();
		 header.put("User-Agent", HttpClientService.USER_AGENT);
		 String serviceUrl = "https://gall.dcinside.com/board/view/?id=dcbest&no=39198&page=1" ;
		 Document doc = communityHttpClientService.get(serviceUrl, header,  null, new ResponseCallBack<Document>(){
				public Document process(int statusCode, String responseString) {	
				    System.out.println( "status : " + statusCode );
				    //System.out.println( responseString );
					return Jsoup.parse( responseString ); 	
				}});

			Elements eles = doc.select(".write_div div>img");
			for( Element ele : eles ) { 				
				String source = ele.attr("src");
				System.out.println(">> " + source);
			}

	}

	@Test
	public void testDownload(){
		String source = "https://dcimg6.dcinside.co.kr/viewimage.php?id=2fb8d133f1db3eb362bdd9b10f&no=24b0d769e1d32ca73feb83fa11d02831b9f69be2ae2fc8e2b8c80de7dc94e75bcb241d181905a2b7c68e7f484dc696548dabe82f79820abfcd3188160e622d87afea36cf8e12fdb940cf875e2b46b662be375e";
		try{ 
			URL url = new URL(source);
			downloadFromUrl(url);
		} catch (Exception e ){
			
		}

	}

	private void downloadFromUrl(URL url) throws Exception { 
		System.out.println( "---------------------" );
		//InputStream inputStream = null;
		try { 
			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"; 
			URLConnection con = url.openConnection(); 
			con.setRequestProperty("User-Agent", USER_AGENT); 

			String contentDisposition = con.getHeaderField("Content-Disposition");
			System.out.println( contentDisposition );
			System.out.println( con.getHeaderFields() );

			String filename = null;
			if(StringUtils.isNotBlank(contentDisposition) ){
				String[] cd = org.springframework.util.StringUtils.tokenizeToStringArray(contentDisposition, ";");
				System.out.println( cd );
				if( cd.length == 2 ){
					filename = cd[1];
					filename = StringUtils.removeStart(filename, "filename=");
				}
			} 
			if(filename == null )
			 	filename = java.util.UUID.randomUUID().toString() ;

			//File file = new File(filename);
			
			System.out.println( filename );
		}finally { 
		}
	}
	
}
