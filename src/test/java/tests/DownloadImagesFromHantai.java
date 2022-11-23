package tests;

import java.net.URL;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.image.ImageService;
import architecture.community.query.CustomQueryService;
import architecture.community.services.HttpClientService;
import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack;
import architecture.community.streams.StreamsService;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.web.gateway.annotation.ScriptData;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;


public class DownloadImagesFromHantai {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Inject
	@Qualifier("communityHttpClientService")
	private HttpClientService communityHttpClientService;
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("repository")
	private Repository repository;
	
	
	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;
				

	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	
	@ScriptData
	public Object download (HttpServletRequest request, HttpServletResponse response ) throws Exception {  
		
		String title = "keep-this-a-secret-from-mom";
		java.util.List list = new java.util.ArrayList();
		
		for ( int i = 43 ;  i <= 47 ; i ++ ) {			
		java.util.Map row = new java.util.HashMap();
       
		
		Map<String, String> values = new java.util.HashMap<String, String>();
	    //value.put("q", "Seoul");
	    
		
		//String serviceUrl = "https://mangahentai.me/manga-hentai/"+ title +"/chapter-" + i + "/"; 
		String serviceUrl = "https://bonmanga.com/manga/"+ title +"/chapter-" + i + "/";  

        row.put("page", serviceUrl );
        
		Document doc = communityHttpClientService.get(serviceUrl, null, new ResponseCallBack<Document>(){
		public Document process(int statusCode, String responseString) {				
			Document _document = Jsoup.parse( responseString ); 		
			row.put("response", responseString );
			return _document;
		}});	
		
		
		
		User user = new UserTemplate(1L); 
		Elements eles = doc.select(".reading-content .page-break img"); 
		java.util.List list2 = new java.util.ArrayList();
		for( Element ele : eles ) { 				
			String source = ele.attr("src").trim();
			String filename = FilenameUtils.getName(source);			
			try {
				URL url = new URL(source);
				list2.add(url);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}			
		}		
		row.put("images", list2 );
		}
		
		return list ;
	} 
}
