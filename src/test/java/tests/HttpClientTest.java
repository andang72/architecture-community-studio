package tests;

import org.apache.tika.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import architecture.community.services.support.PooledHttpClientSupport;

public class HttpClientTest extends PooledHttpClientSupport {

	
	@Test
	public void testConnection() throws Exception {
		
		String serviceUrl = "https://manytoon.com/comic/young-boss/chapter-25/";
		Document doc = this.get(serviceUrl, null, new ResponseCallBack<Document>(){
		public Document process(int statusCode, String responseString) {				
			Document _document = Jsoup.parse( responseString ); 				
			return _document;
		}});	
		
		log.debug(doc.title());
		Elements eles = doc.select(".reading-content .page-break img");
		for( Element ele : eles ) { 				
			String source = ele.attr("src").trim();
			String name = FilenameUtils.getName(source);
			log.debug(name);
			log.debug(source);
			//FileUtils.copyURLToFile(source, destination);			
		}
	}
	
}
