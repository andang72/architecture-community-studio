package architecture.community.web.spring.controller.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.attachment.AttachmentService;
import architecture.community.comment.CommentService;
import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.streams.DefaultStreamMessage;
import architecture.community.streams.DefaultStreamThread;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamMessageNotFoundException;
import architecture.community.streams.StreamThread;
import architecture.community.streams.StreamThreadNotFoundException;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsNotFoundException;
import architecture.community.streams.StreamsService;
import architecture.community.user.AvatarService;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.util.SecurityHelper;
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;

@Controller("community-me-streams-data-controller") 
public class MeStreamsDataController {
 
    private Logger log = LoggerFactory.getLogger(MeStreamsDataController.class);
	
	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;

    @Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

    /**
	 * STREAMS ME API
	******************************************/

	/**
	 * 
	 * /data/streams/{streamsId}/threads/list.json
	 * 
	 * @param fields
	 * @param dataSourceRequest
	 * @return
	 * @throws StreamsNotFoundException
	 */
	@RequestMapping(value = "/data/streams/me/threads/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList listThread (
			@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
			@RequestBody DataSourceRequest dataSourceRequest,
			NativeWebRequest request) throws StreamsNotFoundException {	
		
		boolean fullbody = org.apache.commons.lang3.StringUtils.contains(fields, "body");
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		
		dataSourceRequest.getData().put("objectType",Models.STREAMS.getObjectType());
		dataSourceRequest.getData().put("objectId", streams.getStreamId() );
		
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.COUNT_STREAM_THREAD_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_IDS_BY_REQUEST");
		List<Long> threadIDs = customQueryService.list(dataSourceRequest, Long.class);
		
		List<StreamThread> list = new ArrayList<>(threadIDs.size());
		for(long threadId : threadIDs) {
			try {
				list.add( new StreamsDataController.StreamThreadView( streamsService.getStreamThread(threadId) , fullbody ) );
			} catch (StreamThreadNotFoundException ignore){}
		}
		return new ItemList(list, totalCount);
	}



	@RequestMapping(value = "/data/streams/me/threads/add.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public StreamThread addThread (@RequestBody DefaultStreamMessage newMessage,  NativeWebRequest request) throws StreamsNotFoundException {	
		User user = SecurityHelper.getUser();
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		if (newMessage.getThreadId() < 1 && newMessage.getMessageId() < 1) {
			StreamMessage rootMessage = streamsService.createMessage(Models.STREAMS.getObjectType(), streams.getStreamId(), user);
			rootMessage.setSubject(newMessage.getSubject());
			rootMessage.setBody(newMessage.getBody());
			StreamThread thread = streamsService.createThread(rootMessage.getObjectType(), rootMessage.getObjectId(), rootMessage );
			streamsService.addThread(rootMessage.getObjectType(), rootMessage.getObjectId(), thread);
			return thread;
		}
		return null;
	}
	
	@RequestMapping(value = "/data/streams/me/threads/{threadId:[\\p{Digit}]+}/delete.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public Result deleteThread (
			@PathVariable Long threadId,  NativeWebRequest request) throws NotFoundException {	
		User user = SecurityHelper.getUser();
		Result result = Result.newResult();
		if (threadId > 0L) {
			StreamThread thread = streamsService.getStreamThread(threadId);
			if( thread.getRootMessage().getUser().getUserId() == user.getUserId() ) {
				streamsService.deleteThread(thread);
				result.setCount(1);
			}
		}
		return result;
	}
	
	@Secured({ "ROLE_USER" })
	@RequestMapping(value = "/data/streams/me/messages/add.json", method = { RequestMethod.POST })
	@ResponseBody
	public StreamMessage addMessage(@RequestBody DefaultStreamMessage newMessage, NativeWebRequest request) { 
		User user = SecurityHelper.getUser();
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		if (newMessage.getThreadId() < 1 && newMessage.getMessageId() < 1) {
			StreamMessage rootMessage = streamsService.createMessage(Models.STREAMS.getObjectType(), streams.getStreamId(), user);
			rootMessage.setSubject(newMessage.getSubject());
			rootMessage.setBody(newMessage.getBody());
			StreamThread thread = streamsService.createThread(rootMessage.getObjectType(), rootMessage.getObjectId(), rootMessage );
			streamsService.addThread(rootMessage.getObjectType(), rootMessage.getObjectId(), thread);
			return thread.getRootMessage();
		}
		return newMessage;
	}

}
