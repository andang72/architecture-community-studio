package architecture.community.web.spring.controller.view;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.View;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.audit.event.AuditLogEvent;
import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.page.Page;
import architecture.community.page.PageNotFoundException;
import architecture.community.page.PageService;
import architecture.community.services.CommunityGroovyService;
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.util.SecurityHelper;
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.util.ServletUtils;

@Controller("community-display-pdf-controller")
@RequestMapping("/display")  
public class PdfViewerController implements ServletContextAware {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required=false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;
	
	@Autowired(required=false)
	@Qualifier("pageService")
	private PageService pageService; 
	
	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	 
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	@Autowired(required=false)
	@Qualifier("communityGroovyService")
	private CommunityGroovyService communityGroovyService;	
	
	public static final String DEFAULT_PDF_VIEWER_PAGE = "pdf-reader.html";
	public static final Integer DEFAULT_PDF_VIEWER_PAGE_VERSION = 1;
	
	private ServletContext servletContext; 

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	} 


	@RequestMapping(value = {"/pdf/{filename:.+}"}, method = { RequestMethod.POST, RequestMethod.GET })
	public String pdf (
		@PathVariable String filename,
		HttpServletRequest request, 
	    HttpServletResponse response, 
	    Model model ) throws NotFoundException {
		
		SharedLink link = sharedLinkService.getSharedLink(filename);  
		Attachment attachment = attachmentService.getAttachment(link.getObjectId());    
		model.addAttribute("link", link);
		model.addAttribute("attachment", attachment);  
		
		String view = "pdf-viewer"; 
		
		try { 
			Page page = pageService.getPage(DEFAULT_PDF_VIEWER_PAGE, DEFAULT_PDF_VIEWER_PAGE_VERSION ); 
			model.addAttribute("__page", page);   
			if( StringUtils.isNotEmpty(page.getScript()) && communityGroovyService!=null) {
				View _view = communityGroovyService.getService(page.getScript(), View.class);
				try {
					_view.render( model.asMap(), request, response);
					
				} catch (Exception e) {  
					log.warn(e.getMessage(), e);
				}
			}
			if( StringUtils.isNotEmpty( page.getTemplate() ) )
			{
				view = page.getTemplate(); 
				if(StringUtils.endsWith(view, ".ftl")) {
					ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
					view = StringUtils.removeEnd(view, ".ftl");	
				}else if (StringUtils.endsWith(view, ".jsp")) {
					view = StringUtils.removeEnd(view, ".jsp");
				}
			}
			if(communitySpringEventPublisher!=null)
				communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, this))
					.objectTypeAndObjectId(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId() )
					.action(AuditLogEvent.READ)
					.code(this.getClass().getName())
					.resource(page.getName()).build()); 
				
			if( viewCountService != null ) {
				viewCountService.addViewCount(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId()); 
			}
		} catch (PageNotFoundException e) {
			ServletUtils.setContentType(ServletUtils.DEFAULT_HTML_CONTENT_TYPE, response);
			if(communitySpringEventPublisher!=null)
				communitySpringEventPublisher.fireEvent((new AuditLogEvent.Builder(request, response, SecurityHelper.getAuthentication()))
					.objectTypeAndObjectId(Models.PAGE.getObjectType(), -1L)
					.action(AuditLogEvent.READ)
					.resource(DEFAULT_PDF_VIEWER_PAGE).build());
		} 
		
		return view;
	}
		
}
