package architecture.community.web.spring.controller.data.secure.mgmt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.comment.Comment;
import architecture.community.comment.CommentService;
import architecture.community.comment.DefaultComment;
import architecture.community.exception.NotFoundException;
import architecture.community.model.ModelObjectTreeWalker;
import architecture.community.model.ModelObjectTreeWalker.ObjectLoader;
import architecture.community.model.Models;
import architecture.community.model.Property;
import architecture.community.query.CustomQueryService;
import architecture.community.query.CustomTransactionCallbackWithoutResult;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.streams.DefaultStreamMessage;
import architecture.community.streams.DefaultStreamThread;
import architecture.community.streams.DefaultStreams;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamMessageNotFoundException;
import architecture.community.streams.StreamThread;
import architecture.community.streams.StreamThreadNotFoundException;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsNotFoundException;
import architecture.community.streams.StreamsService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.StreamsDataController.StreamThreadView;
import architecture.community.web.spring.controller.data.Utils;
import architecture.community.web.spring.controller.data.model.Bag;
import architecture.ee.util.StringUtils;
/**
 * 
 * 
 * 

POST – Create , Select
GET – Read/Retrieve
PUT/PATCH – Update
DELETE – Delete

 * @author donghyuck.son
 *
 */
@Controller("community-mgmt-resources-streams-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesStreamsDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;
	
	@Inject
	@Qualifier("commentService")
	private CommentService commentService;

	
	/**
	 * 
	 * STREAMS API
	 * OPJECT TYPE : 20
	 *  
	******************************************/
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/streams", "/streams/list.json"}, method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ItemList getStreams(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {  
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.COUNT_STREAMS_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.SELECT_STREAMS_IDS_BY_REQUEST"); 
		List<Long> IDs = customQueryService.list(dataSourceRequest, Long.class); 
		List<Streams> items = new ArrayList<Streams>(totalCount);
		for( Long streamId : IDs ) {
			try {
				Streams streams = streamsService.getStreamsById(streamId);
				items.add(streams); 
			} catch (NotFoundException e) {
			}
		} 
		return new ItemList(items, totalCount ); 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/streams/{streamId:[\\p{Digit}]+}"}, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Streams getStreamsById(@PathVariable Long streamId, NativeWebRequest request) throws NotFoundException {  
		return streamsService.getStreamsById(streamId);
	} 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/streams/{streamId:[\\p{Digit}]+}"}, method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Streams saveOrUpdateStreams(@RequestBody DefaultStreams streams, @PathVariable Long streamId,  NativeWebRequest request) throws NotFoundException {  
		
		DefaultStreams streamToUse = streams;
		if(streamToUse.getStreamId() > 0  ) { 
			streamToUse = (DefaultStreams)streamsService.getStreamsById(streams.getStreamId()); 
			if( !StringUtils.isNullOrEmpty(streams.getName()) && !StringUtils.equals(streamToUse.getName(), streams.getName()) )
			{
				streamToUse.setName(streams.getName());
			}
			if( !StringUtils.isNullOrEmpty(streams.getDisplayName()) && !StringUtils.equals(streamToUse.getDisplayName(), streams.getDisplayName()))
			{
				streamToUse.setDisplayName(streams.getDisplayName());
			}
			if( !StringUtils.isNullOrEmpty(streams.getDescription()) && !StringUtils.equals(streamToUse.getDescription(), streams.getDescription()) )
			{
				streamToUse.setDescription(streams.getDescription());
			} 
		}else {
			//streamToUse.setUser( SecurityHelper.getUser() );  
		} 
		streamsService.saveOrUpdate(streamToUse);
		return streamToUse;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/streams/{streamId:[\\p{Digit}]+}/properties/list.json"}, method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public List<Property> getStreamsProperties(@PathVariable Long streamId, NativeWebRequest request)
			throws NotFoundException { 
		if (streamId <= 0) {
			return Collections.EMPTY_LIST;
		}
		Streams streams = streamsService.getStreamsById(streamId);
		Map<String, String> properties = streams.getProperties(); 
		return Utils.toList(properties);
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/streams/{streamId:[\\p{Digit}]+}/properties"}, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public List<Property> getStreamsProperties_v2(@PathVariable Long streamId, NativeWebRequest request)
			throws NotFoundException { 
		return getStreamsProperties(streamId, request);
	}
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {  "/streams/{streamId:[\\p{Digit}]+}/properties", "/streams/{streamId:[\\p{Digit}]+}/properties/update.json" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Property> updateStreamProperties(
			@PathVariable Long streamId, 
			@RequestBody List<Property> newProperties, 
			NativeWebRequest request) throws NotFoundException { 
		
		Streams streams = streamsService.getStreamsById(streamId);
		Map<String, String> properties = streams.getProperties(); 
		// update or create
		for (Property property : newProperties) {
			properties.put(property.getName(), property.getValue().toString());
		}
		
		if (newProperties.size() > 0) {
			streamsService.saveOrUpdate(streams);
		} 
		return Utils.toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {  "/streams/{streamId:[\\p{Digit}]+}/properties" ,  "/streams/{streamId:[\\p{Digit}]+}/properties/delete.json"  }, method = { RequestMethod.DELETE }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Property> deleteStreamProperties(
			@PathVariable Long streamId, 
			@RequestBody List<Property> newProperties, 
			NativeWebRequest request) throws NotFoundException {
		 
		Streams streams = streamsService.getStreamsById(streamId);
		Map<String, String> properties = streams.getProperties(); 
		for (Property property : newProperties) {
			properties.remove(property.getName());
		}
		if (newProperties.size() > 0) {
			streamsService.saveOrUpdate(streams);
		} 
		return Utils.toList(properties);
	}
	
		
	/**
	 * 
	 * GET /data/secure/mgmt/streams/{streamsId}/threads
	 * 
	 * @param streamsId
	 * @param request
	 * @return
	 * @throws StreamsNotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/threads", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ItemList getThreads (
			@PathVariable Long streamId, 
			@RequestParam(value = "skip", defaultValue = "0", required = false) int skip,
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "pageSize", defaultValue = "0", required = false) int pageSize, 
			NativeWebRequest request) throws StreamsNotFoundException {	
		log.debug(" skip: {}, page: {}, pageSize: {}", skip, page, pageSize ); 
		Streams streams = streamsService.getStreamsById(streamId); 
		List<StreamThread> list; 
		int totalSize = streamsService.getStreamThreadCount(Models.STREAMS.getObjectType(), streams.getStreamId()); 
		if( pageSize == 0 && page == 0){
			list = streamsService.getStreamThreads(Models.STREAMS.getObjectType(), streams.getStreamId() );
		}else{
			list = streamsService.getStreamThreads(Models.STREAMS.getObjectType(),  streams.getStreamId(), skip, pageSize);
		} 
		return new ItemList(list, totalSize);
	}
	
	/**
	 * 
	 * POST /data/secure/mgm/streams/{streamsId}/threads
	 * 
	 * @param fields
	 * @param dataSourceRequest
	 * @return
	 * @throws StreamsNotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/threads", method = { RequestMethod.POST},  produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ItemList getThreads (
			@PathVariable Long streamId, 
			@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
			@RequestBody DataSourceRequest dataSourceRequest,
			NativeWebRequest request) throws StreamsNotFoundException {	
		
		boolean fullbody = org.apache.commons.lang3.StringUtils.contains(fields, "body");
		Streams streams = streamsService.getStreamsById(streamId);
		dataSourceRequest.getData().put("objectType",Models.STREAMS.getObjectType());
		dataSourceRequest.getData().put("objectId", streams.getStreamId() ); 
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.COUNT_STREAM_THREAD_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_IDS_BY_REQUEST");
		List<Long> threadIDs = customQueryService.list(dataSourceRequest, Long.class);
		
		List<StreamThread> list = new ArrayList<>(threadIDs.size());
		for(long threadId : threadIDs) {
			try {
				list.add( new StreamThreadView( streamsService.getStreamThread(threadId) , fullbody ) );
			} catch (StreamThreadNotFoundException ignore){}
		}
		return new ItemList(list, totalCount);
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/threads", method = { RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result deleteThreads (
			@PathVariable Long streamId,  
			@RequestBody Bag bag, 
			NativeWebRequest request) throws NotFoundException {	
		
		User user = SecurityHelper.getUser();
		Result result = Result.newResult();
		
		final List<Long> threadIDs = bag.getItems();
		customQueryService.execute(new CustomTransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(CustomQueryJdbcDao dao) {
				
				for( Long threadId : threadIDs)
				{
					dao.getExtendedJdbcTemplate().update(dao.getBoundSql("COMMUNITY_STREAMS.DELETE_STREAM_MESSAGE_BY_THREAD_ID").getSql(),
						new SqlParameterValue(Types.NUMERIC, threadId ));
					dao.getExtendedJdbcTemplate().update(dao.getBoundSql("COMMUNITY_STREAMS.DELETE_STREAM_THREAD").getSql(),
						new SqlParameterValue(Types.NUMERIC, threadId ));	
				}
				
			}});
		
		streamsService.clearCache();
		
		return result;
	}
	
	
	
	
	/**
	 * create new thread into streams
	 *  
	 * POST /data/secure/mgmt/streams/{streamId}/threads/0
	 * 
	 * @param streamId
	 * @param newMessage
	 * @param request
	 * @return
	 * @throws StreamsNotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/threads/0", method = { RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public StreamThread addThread (
			@PathVariable Long streamId, 
			@RequestBody DefaultStreamMessage newMessage,  NativeWebRequest request) throws StreamsNotFoundException {	
		
		User user = SecurityHelper.getUser();
		Streams streams = streamsService.getStreamsById(streamId); 
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
	
	/**
	 * delete thread by threadId
	 * 
	 * DELETE /data/secure/mgmt/streams/{streamId}/threads/{threadId}
	 * 
	 * @param threadId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/threads/{threadId:[\\p{Digit}]+}", method = { RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result deleteThread (
			@PathVariable Long threadId,  
			NativeWebRequest request) throws NotFoundException {	
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
	 

	
	/**
	 * move threads to other stream.
	 * 
	 * POST /data/secure/mgmt/streams/{streamId}/threads/move
	 * 
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/threads/move", method = { RequestMethod.POST, RequestMethod.PATCH}, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result moveThreads (
			@PathVariable Long streamId,  
			@RequestBody Bag move, 
			NativeWebRequest request) throws NotFoundException {	
		
		User user = SecurityHelper.getUser();
		Result result = Result.newResult();
							
		
		final int objectTypeToUse = move.getObjectType();
		final long objectIdToUse = move.getToObjectId();
		final List<Long> threadIDs = move.getItems();
		customQueryService.execute(new CustomTransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(CustomQueryJdbcDao dao) {
				
				for( Long threadId : threadIDs)
				{
					dao.getExtendedJdbcTemplate().update(dao.getBoundSql("COMMUNITY_STREAMS.MOVE_STREAM_THREAD").getSql(), 
						new SqlParameterValue(Types.NUMERIC, objectTypeToUse ),
						new SqlParameterValue(Types.NUMERIC, objectIdToUse ),
						new SqlParameterValue(Types.NUMERIC, threadId )
					);		
					dao.getExtendedJdbcTemplate().update(dao.getBoundSql("COMMUNITY_STREAMS.MOVE_STREAM_MESSAGE").getSql(), 
						new SqlParameterValue(Types.NUMERIC, objectTypeToUse ),
						new SqlParameterValue(Types.NUMERIC, objectIdToUse ),
						new SqlParameterValue(Types.NUMERIC, threadId )
					);
				}
				
			}});
		
		streamsService.clearCache();
		
		return result;
	}
	
	
	/**
	 * new message 
	 * 
	 * POST /data/secure/mgmt/streams/{streamId}/messages/0
	 * 
	 * @param newMessage
	 * @param request
	 * @return
	 * @throws NotFoundException 
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/streams/{streamId:[\\p{Digit}]+}/messages/0", method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StreamMessage addMessage(
			@PathVariable Long streamId,
			@RequestBody DefaultStreamMessage newMessage, 
			NativeWebRequest request) throws NotFoundException { 
		User user = SecurityHelper.getUser();
		Streams streams = streamsService.getStreamsById(streamId); 
		if (newMessage.getThreadId() < 1 && newMessage.getMessageId() < 1) {
			StreamMessage rootMessage = streamsService.createMessage(Models.STREAMS.getObjectType(), streams.getStreamId(), user);
			rootMessage.setSubject(newMessage.getSubject());
			rootMessage.setBody(newMessage.getBody());
			rootMessage.setKeywords(newMessage.getKeywords());
			rootMessage.setTags(newMessage.getTags());
			StreamThread thread = streamsService.createThread(rootMessage.getObjectType(), rootMessage.getObjectId(), rootMessage );
			streamsService.addThread(rootMessage.getObjectType(), rootMessage.getObjectId(), thread);
			return thread.getRootMessage();
		}
		return newMessage;
	}
	
	/**
	 * get thread by threadId
	 * 
	 * GET /data/secure/mgmt/threads/{threadId}
	 * 
	 * @param threadId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/threads/{threadId:[\\p{Digit}]+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StreamThread getThread(@PathVariable Long threadId, NativeWebRequest request) throws NotFoundException {
		if (threadId < 1) {
			throw new StreamThreadNotFoundException();
		}
		
		StreamThread t = streamsService.getStreamThread(threadId);
		
		StreamThreadView v = new StreamThreadView(streamsService.getStreamThread(threadId), true );
		
		/*
		 * if( t.getObjectType() == Models.STREAMS.getObjectType() && t.getObjectId() >
		 * 0 ) { Streams s = streamsService.getStreamsById(t.getObjectType()); boolean
		 * lightbox = Boolean.parseBoolean(
		 * s.getProperties().getOrDefault("filters.lightbox", "false") ); }
		 */	
		return new StreamThreadView(streamsService.getStreamThread(threadId), true );
	}
	
	
	/**
	 * get message
	 * 
	 * GET /data/secure/mgmt/messages/{messageId}
	 * @param messageId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/messages/{messageId:[\\p{Digit}]+}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StreamMessage getMessage(@PathVariable Long messageId, NativeWebRequest request) throws NotFoundException {
		if (messageId < 1) {
			throw new StreamMessageNotFoundException();
		}
		return streamsService.getStreamMessage(messageId);
	}	
	
	/**
	 * update message 
	 * PUT /data/secure/mgmt/messages/{messageId}
	 * @param newMessage
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/messages/{messageId:[\\p{Digit}]+}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StreamMessage updateMessage(
			@PathVariable Long messageId,
			@RequestBody DefaultStreamMessage newMessage, 
			NativeWebRequest request) throws NotFoundException { 
		
		User user = SecurityHelper.getUser();	 
		
		StreamMessage message = streamsService.getStreamMessage(newMessage.getMessageId());		
		message.setSubject(newMessage.getSubject());
		message.setBody(newMessage.getBody());
		message.setKeywords(newMessage.getKeywords());
		message.setTags(newMessage.getTags());
		
		streamsService.updateMessage(message);
		
		
		
		return message;
	} 
	
	/**
	 * remove message 
	 * 
	 * DELETE /data/secure/mgmt/messages/{messageId}
	 * @param newMessage
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/messages/{messageId:[\\p{Digit}]+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Result deleteeMessage(
			@PathVariable Long messageId, 
			@RequestParam(value = "recursive", defaultValue = "true", required = false) boolean recursive,
			NativeWebRequest request) throws NotFoundException { 
		
		Result result = Result.newResult();;
		User user = SecurityHelper.getUser();	 
		StreamMessage message = streamsService.getStreamMessage(messageId);
		StreamThread thread = streamsService.getStreamThread(message.getThreadId());
		streamsService.deleteMessage(thread, message, recursive);		
		return result;
	} 
	
	
	/**
	 * 
	 * COMMENTS API
	 * OPJECT TYPE : 20
	 *   
	******************************************/
	
	/**
	 * POST,PUT /data/secure/mgmt/messages/{messageId}/comments/0 
	 * 
	 * @param messageId
	 * @param reqeustData
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/messages/{messageId:[\\p{Digit}]+}/comments/0"}, method = { RequestMethod.POST , RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result addMessageComment(
			@PathVariable Long messageId,
			@RequestBody DataSourceRequest reqeustData,
			HttpServletRequest request, ModelMap model) {
		Result result = Result.newResult();
		try {
			User user = SecurityHelper.getUser();
			String address = request.getRemoteAddr();
			String name = reqeustData.getDataAsString("name", null);
			String email = reqeustData.getDataAsString("email", null);
			String text = reqeustData.getDataAsString("text", null);
			Long parentCommentId = reqeustData.getDataAsLong("parentCommentId", 0L);
			StreamMessage message = streamsService.getStreamMessage(messageId);
			Comment newComment = commentService.createComment(Models.STREAMS_MESSAGE.getObjectType(), message.getMessageId(), user, text);
			newComment.setIPAddress(address);
			if (!StringUtils.isNullOrEmpty(name))
				newComment.setName(name);
			if (!StringUtils.isNullOrEmpty(email))
				newComment.setEmail(email);
			if (parentCommentId > 0) {
				Comment parentComment = commentService.getComment(parentCommentId);
				commentService.addComment(parentComment, newComment);
			} else {
				commentService.addComment(newComment);
			}
			result.setCount(1);
		} catch (Exception e) {
			result.setError(e);
		}
		return result;
	}

	@RequestMapping(value = "/messages/{messageId:[\\p{Digit}]+}/comments", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ItemList getComments(@PathVariable Long messageId, NativeWebRequest request)
			throws NotFoundException {		
		StreamMessage message = streamsService.getStreamMessage(messageId);
		ModelObjectTreeWalker walker = commentService.getCommentTreeWalker(Models.STREAMS_MESSAGE.getObjectType(), message.getMessageId());
		long parentId = -1L;
		int totalSize = walker.getChildCount(parentId);
		List<Comment> list = walker.children(parentId, new ObjectLoader<Comment>() {
			public Comment load(long commentId) throws NotFoundException {
				return commentService.getComment(commentId);
			}

		});
		return new ItemList(list, totalSize);
	}

	
	@RequestMapping(value = "/messages/{messageId:[\\p{Digit}]+}/comments/{commentId:[\\p{Digit}]+}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ItemList getChildComments(@PathVariable Long messageId,
			@PathVariable Long commentId, NativeWebRequest request) throws NotFoundException {

		StreamMessage message = streamsService.getStreamMessage(messageId);
		ModelObjectTreeWalker walker = commentService.getCommentTreeWalker(Models.STREAMS_MESSAGE.getObjectType(), message.getMessageId());
		int totalSize = walker.getChildCount(commentId);		
		List<Comment> list = walker.children(commentId, new ObjectLoader<Comment>() {
			public Comment load(long commentId) throws NotFoundException {
				return commentService.getComment(commentId);
			}
		});
		return new ItemList(list, totalSize);
	}

	

	

	/**
	 * POST /data/secure/mgmt/comments/{commentId}
	 * 
	 * @return
	 * @throws NotFoundException 
	 * @throws BoardMessageNotFoundException
	 * @throws BoardNotFoundException
	 */
	@RequestMapping(value = "/comments/{commentId:[\\p{Digit}]+}", method = {RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Comment addComment(
			@PathVariable Long commentId,
			@RequestBody DefaultComment comment, 
			HttpServletRequest request,
			ModelMap model) throws NotFoundException {

		User user = SecurityHelper.getUser();
		String address = request.getRemoteAddr();

		Comment commentToUse = commentService.getComment(commentId);
		commentToUse.setBody(comment.getBody());
		commentToUse.setIPAddress(address);
		commentService.update(comment);
		return commentService.getComment(commentId);
			
	}
	
	
	/**
	 * remove comment 
	 * 
	 * DELETE /data/secure/mgmt/comments/{commentId}
	 * 
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/comments/{commentId:[\\p{Digit}]+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Result deleteComment(
			@PathVariable Long commentId, 
			@RequestParam(value = "recursive", defaultValue = "true", required = false) boolean recursive,
			NativeWebRequest request) throws NotFoundException { 
		
		Result result = Result.newResult();;
		User user = SecurityHelper.getUser();	 
		
		Comment commentToUse = commentService.getComment(commentId);
		commentService.delete(commentToUse);
		
		return result;
	} 
	
	
}
