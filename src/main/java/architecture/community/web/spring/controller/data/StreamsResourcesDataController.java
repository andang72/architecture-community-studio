package architecture.community.web.spring.controller.data;

import java.io.IOException;
import java.sql.Types;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.exception.NotFoundException;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.query.ParameterValue;
import architecture.community.share.SharedLinkService;
import architecture.community.streams.StreamsService;
import architecture.community.tag.TagService;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;

@Controller("community-streams-resources-data-controller") 
public class StreamsResourcesDataController extends AbstractResourcesDataController{

	private Logger log = LoggerFactory.getLogger(StreamsResourcesDataController.class); 

	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;	
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	
	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;
	
	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
	
	public StreamsResourcesDataController() {
		
	}


	/**
	 * 
	 * 
	 * @param objectType
	 * @param objectId
	 * @param fields
	 * @param request
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 */ 
	@RequestMapping(value = "/data/messages/{messageId:[\\p{Digit}]+}/files", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public ItemList getFiles ( 
    		@PathVariable Long messageId,
    		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
    		NativeWebRequest request) throws NotFoundException, IOException {  		 
		
		boolean includeLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.getParameters().add(new ParameterValue( 0, "OBJECT_TYPE", Types.INTEGER, Models.STREAMS_MESSAGE ));	
		dataSourceRequest.getParameters().add(new ParameterValue( 1, "OBJECT_ID", Types.NUMERIC, messageId ));			
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_ATTACHMENT_BY_OBJECT_TYPE_AND_OBJECT_ID");		
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_ATTACHMENT_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID");	
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class);		
		List<Attachment> attachments = getAttachments(items, includeLink, includeTags);
		return new ItemList(attachments, totalCount );
    }  
	
}
