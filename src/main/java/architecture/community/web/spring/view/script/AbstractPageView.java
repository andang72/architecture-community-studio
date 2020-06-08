package architecture.community.web.spring.view.script;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.community.web.util.ServletUtils;

public abstract class AbstractPageView extends ScriptSupport implements PageView {

	protected Logger log = LoggerFactory.getLogger(getClass());  
 
	private String contentType = ServletUtils.DEFAULT_HTML_CONTENT_TYPE;
	
	public String getContentType() { 
		return contentType ;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("Rendering view {} with model '{}'", getClass().getName(), model );
		}
		prepareResponse(request, response);
		renderMergedOutputModel((Map<String, ?>) model, request, response); 
	}
	
	protected abstract void renderMergedOutputModel( Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		
	}
	
}