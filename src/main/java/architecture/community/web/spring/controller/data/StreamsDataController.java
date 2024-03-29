package architecture.community.web.spring.controller.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import architecture.ee.service.ConfigService;

@Controller("community-streams-data-controller")
public class StreamsDataController {

	private Logger log = LoggerFactory.getLogger(StreamsDataController.class);

	@Autowired(required = false)
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired(required = false)
	@Qualifier("avatarService")
	private AvatarService avatarService;

	@Autowired(required = false)
	@Qualifier("userManager")
	private UserManager userManager;

	@Autowired(required = false)
	@Qualifier("streamsService")
	private StreamsService streamsService;

	@Inject
	@Qualifier("commentService")
	private CommentService commentService;

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;

	@Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Inject
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;

	public StreamsDataController() {

	}

	/**
	 * STREAMS API
	 ******************************************/
	/**
	 * 
	 * POST /data/streams/{streamsId}/list.json
	 * 
	 * @param streamsId
	 * @param request
	 * @return
	 * @throws StreamsNotFoundException
	 */
	@RequestMapping(value = { "/data/streams/list.json" }, method = { RequestMethod.POST })
	@ResponseBody
	public ItemList getStreams(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {

		dataSourceRequest.setStatement("COMMUNITY_STREAMS.COUNT_STREAMS_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);

		dataSourceRequest.setStatement("COMMUNITY_STREAMS.SELECT_STREAMS_IDS_BY_REQUEST");
		List<Long> IDs = customQueryService.list(dataSourceRequest, Long.class);

		List<Streams> items = new ArrayList<Streams>(totalCount);
		for (Long streamId : IDs) {
			try {
				Streams streams = streamsService.getStreamsById(streamId);
				items.add(streams);
			} catch (NotFoundException e) {
			}
		}
		return new ItemList(items, totalCount);
	}

	/**
	 * GET /data/streams/:streamsId
	 * 
	 * @param streamId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = { "/data/streams/{streamId:[\\p{Digit}]+}" }, method = {
			RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Streams getStreamsById(@PathVariable Optional<Long> streamId, NativeWebRequest request)
			throws NotFoundException {
		if (streamId.isPresent())
			return streamsService.getStreamsById(streamId.get());
		else
			throw new NotFoundException("name missing.");
	}

	/**
	 * GET /data/streams/streams-by-name/:name
	 * return stream by name
	 * 
	 * @param name
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = { "/data/streams-by-name/{name}" }, method = {
			RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Streams getStreamsByName(@PathVariable Optional<String> name, NativeWebRequest request)
			throws NotFoundException {
		if (name.isPresent())
			return streamsService.getStreamsByName(name.get());
		else
			throw new NotFoundException("name missing.");

	}

	/**
	 * LIST : POST /data/streams/{streamId}/threads
	 * 
	 * @param streamId
	 * @param dataSourceRequest
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = { "/data/streams/{streamId:[\\p{Digit}]+}/threads" }, method = {
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ItemList getStreamThreads(
			@PathVariable Optional<Long> streamId,
			@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
			@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) throws NotFoundException {

		if (!streamId.isPresent()) {
			throw new NotFoundException("ID missing.");
		}
		StopWatch watch = new StopWatch();
		watch.start("select total count");

		boolean fullbody = StringUtils.contains(fields, "body");
		Streams streams = streamsService.getStreamsById(streamId.get());

		dataSourceRequest.getData().put("objectType", Models.STREAMS.getObjectType());
		dataSourceRequest.getData().put("objectId", streams.getStreamId());
		dataSourceRequest.setStatement("COMMUNITY_STREAMS.COUNT_STREAM_THREAD_BY_REQUEST");

		log.debug("Find threads for objectType: {}, objectId: {} with include body options : {}",
				Models.STREAMS.getObjectType(), streamId.get(), fullbody);

		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		watch.stop();
		watch.start("select IDs");

		dataSourceRequest.setStatement("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_IDS_BY_REQUEST");
		List<Long> threadIDs = customQueryService.list(dataSourceRequest, Long.class);

		watch.stop();
		watch.start("select Objects");

		List<StreamThread> list = new ArrayList<>(threadIDs.size());
		for (long threadId : threadIDs) {
			try {
				list.add(new StreamThreadView(streamsService.getStreamThread(threadId), fullbody));
			} catch (StreamThreadNotFoundException ignore) {
			}
		}

		watch.stop();

		log.info(watch.toString());

		return new ItemList(list, totalCount);
	}

	/**
	 * GET /data/threads/{threadId}
	 * 
	 * @param threadId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = "/data/threads/{threadId:[\\p{Digit}]+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StreamThread geThread(@PathVariable Long threadId, NativeWebRequest request) throws NotFoundException {
		if (threadId < 1) {
			throw new StreamThreadNotFoundException();
		}
		return streamsService.getStreamThread(threadId);
	}

	/**
	 * create new thread into streams
	 * 
	 * CREATE : PUT /data/threads/0
	 * 
	 * @param streamId
	 * @param newMessage
	 * @param request
	 * @return
	 * @throws StreamsNotFoundException
	 */
	@RequestMapping(value = "/data/threads/0", method = { RequestMethod.POST,
			RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StreamThread addThreadMessage(@RequestBody DefaultStreamMessage newMessage, NativeWebRequest request)
			throws StreamsNotFoundException {

		User user = SecurityHelper.getUser();
		log.debug("create new thread by {} ({})", user.getUserId() , SecurityHelper.isUserInRole("ROLE_USER"));

		if (newMessage.getThreadId() < 1 && newMessage.getMessageId() < 1) {
			StreamMessage rootMessage = streamsService.createMessage(newMessage.getObjectType(), newMessage.getObjectId(), user);
			rootMessage.setSubject(newMessage.getSubject());
			rootMessage.setBody(newMessage.getBody());
			StreamThread thread = streamsService.createThread(rootMessage.getObjectType(), rootMessage.getObjectId(), rootMessage);
			streamsService.addThread(rootMessage.getObjectType(), rootMessage.getObjectId(), thread);
			return thread;
		}
		return null;
	}

	/**
	 * GET /data/messages/{messageId}
	 * 
	 * @param messageId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = { "/data/messages/{messageId:[\\p{Digit}]+}",
			"/data/messages/{messageId:[\\p{Digit}]+}/get.json" }, method = RequestMethod.GET)
	@ResponseBody
	public StreamMessage getMessage(@PathVariable Long messageId, NativeWebRequest request) throws NotFoundException {
		if (messageId < 1) {
			throw new StreamMessageNotFoundException();
		}
		return streamsService.getStreamMessage(messageId);
	}

	/**
	 * POST /data/messages/{messageId}
	 * 
	 * @param newMessage
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = { "/data/messages/{messageId:[\\p{Digit}]+}",
			"/data/messages/{messageId:[\\p{Digit}]+}/update.json" }, method = RequestMethod.POST)
	@ResponseBody
	public StreamMessage updateMessage(@RequestBody DefaultStreamMessage newMessage, NativeWebRequest request)
			throws NotFoundException {
		User user = SecurityHelper.getUser();
		StreamMessage message = streamsService.getStreamMessage(newMessage.getMessageId());
		message.setSubject(newMessage.getSubject());
		message.setBody(newMessage.getBody());
		streamsService.updateMessage(message);
		return message;
	}

	public static class StreamMessageView implements StreamMessage {

		@JsonIgnore
		private StreamMessage message;

		@JsonIgnore
		private boolean fullbody;

		@JsonIgnore
		private int maxSummarySize = 60;

		public StreamMessageView(StreamMessage message, boolean fullbody) {
			this.message = message;
			this.fullbody = fullbody;
		}

		@Override
		public Map<String, String> getProperties() {
			return message.getProperties();
		}

		@Override
		public void setProperties(Map<String, String> properties) {
		}

		@Override
		public int getObjectType() {
			return message.getObjectType();
		}

		@Override
		public long getObjectId() {
			return message.getObjectId();
		}

		@Override
		public User getUser() {
			return message.getUser();
		}

		@Override
		public long getParentMessageId() {
			return message.getParentMessageId();
		}

		@Override
		public long getMessageId() {
			return message.getMessageId();
		}

		@Override
		public long getThreadId() {
			return message.getThreadId();
		}

		@Override
		public String getSubject() {
			return message.getSubject();
		}

		@Override
		public String getBody() {
			return fullbody ? message.getBody() : StringUtils.abbreviate(message.getBody(), maxSummarySize);
		}

		@Override
		public void setSubject(String subject) {

		}

		@Override
		public void setBody(String body) {

		}

		@Override
		public void setKeywords(String keywords) {

		}

		@Override
		public String getKeywords() {
			return message.getKeywords();
		}

		@Override
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS+00:00")
		public Date getCreationDate() {
			return message.getCreationDate();
		}

		@Override
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS+00:00")
		public Date getModifiedDate() {
			return message.getModifiedDate();
		}

		@Override
		public void setCreationDate(Date creationDate) {
		}

		@Override
		public void setModifiedDate(Date modifiedDate) {
		}

		public int getAttachmentsCount() {
			if (message instanceof DefaultStreamMessage) {
				return ((DefaultStreamMessage) message).getAttachmentsCount();
			}
			return 0;
		}

		public int getReplyCount() {
			if (message instanceof DefaultStreamMessage) {
				return ((DefaultStreamMessage) message).getReplyCount();
			}
			return 0;
		}

		@Override
		public String getTags() {
			return message.getTags();
		}

		@Override
		public void setTags(String tags) {
		}
	}


	public static class StreamThreadView implements StreamThread {

		private Long prevId;
		private Long nextId;
		@JsonIgnore
		private StreamThread thread;
		@JsonIgnore
		private boolean fullbody;

		public StreamThreadView(StreamThread thread, boolean fullbody) {
			this.thread = thread;
			this.fullbody = fullbody;
		}

		@Override
		public Map<String, String> getProperties() {
			return thread.getProperties();
		}

		@Override
		public void setProperties(Map<String, String> properties) {

		}

		@Override
		public int getObjectType() {
			return thread.getObjectType();
		}

		@Override
		public long getObjectId() {
			return thread.getObjectId();
		}

		@Override
		public long getThreadId() {
			return thread.getThreadId();
		}

		@Override
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS+00:00")
		public Date getCreationDate() {
			return thread.getCreationDate();
		}

		@Override
		public void setCreationDate(Date creationDate) {

		}

		@Override
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS+00:00")
		public Date getModifiedDate() {
			return thread.getModifiedDate();
		}

		@Override
		public void setModifiedDate(Date modifiedDate) {

		}

		@Override
		public StreamMessage getLatestMessage() {
			if (thread.getLatestMessage() == null)
				return null;
			return fullbody ? thread.getLatestMessage() : new StreamMessageView(thread.getLatestMessage(), fullbody);
		}

		@Override
		public void setLatestMessage(StreamMessage latestMessage) {
		}

		@Override
		public StreamMessage getRootMessage() {
			if (thread.getRootMessage() == null)
				return null;
			return fullbody ? thread.getRootMessage() : new StreamMessageView(thread.getRootMessage(), fullbody);
		}

		public void setRootMessage(StreamMessage rootMessage) {

		}

		public String getCoverImgSrc() {
			return ((DefaultStreamThread)thread).getCoverImgSrc();
		}

		@JsonGetter("embedMediaCount")
		public Integer getEmbedMediaCount() {
			return ((DefaultStreamThread) thread).getEmbedMediaCount();
		}

		@JsonGetter("viewCount")
		public int getViewCount() {
			if (this.thread.getThreadId() < 1)
				return -1;
			return ((DefaultStreamThread) thread).getViewCount();
		}

		public Long getPrevId() {
			return prevId;
		}

		public void setPrevId(Long prevId) {
			this.prevId = prevId;
		}

		public Long getNextId() {
			return nextId;
		}

		public void setNextId(Long nextId) {
			this.nextId = nextId;
		}

	}
}
