package architecture.community.web.spring.filter;
 
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import architecture.community.web.util.ServletUtils;

@WebFilter(filterName = "makedownFilter", urlPatterns = "*.md")
public class MakedownViewFilter extends OncePerRequestFilter {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	private ResourceLoader resourceLoader ;
	
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		resourceLoader = new ServletContextResourceLoader(this.getServletContext()); 
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String url = urlPathHelper.getLookupPathForRequest(request);
		Resource resouce = getResourceForUrl(url);
		log.debug("URL : {} - {}", url, resouce.exists() );
		
		// 리소스가 존재하는 경우만 처리하며 이외의 경우는 다음 필터가 적용 되도록 한다. 
		if( resouce.exists() ) {  
			Parser parser = Parser.builder().build();
			Node document = parser.parse(
				FileUtils.readFileToString(resouce.getFile(), ServletUtils.DEFAULT_HTML_ENCODING)		
			);
			HtmlRenderer renderer = HtmlRenderer.builder().build();
			ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
			IOUtils.write(
				renderer.render(document),
				response.getWriter()
			);
			response.flushBuffer();
			return;
		} 
		super.doFilter(request, response, filterChain); 
	} 
	
	/**
	 * url 에 해당한는 리소스를 리턴한다.
	 * @param url
	 * @return
	 */
	protected Resource getResourceForUrl(String url) {
		return resourceLoader.getResource(url);
	} 
	
}
