package architecture.community.streams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.model.Models;
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.streams.dao.StreamsDao;
import architecture.community.streams.event.StreamThreadEvent;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.util.SecurityHelper;

public class CommunityStreamsService implements StreamsService {

	@Inject
	@Qualifier("streamsDao")
	private StreamsDao streamsDao;
	
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
	
	private com.google.common.cache.LoadingCache<Long, Streams> streamsCache = null;
	private com.google.common.cache.LoadingCache<Long, StreamThread> threadCache = null;
	private com.google.common.cache.LoadingCache<Long, StreamMessage> messageCache = null;
	
	private Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	public CommunityStreamsService() { 
		
		streamsCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(60 * 100, TimeUnit.MINUTES).build(		
			new CacheLoader<Long, Streams>(){			
				public Streams load(Long streamId) throws Exception { 
					return streamsDao.getStreamById(streamId);
			}}
		);
		
		threadCache = CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess(60 * 100, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, StreamThread>(){			
					public StreamThread load(Long threadId) throws Exception { 
						return streamsDao.getStreamThreadById(threadId);
				}}
			);
		
		messageCache = CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess(60 * 100, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, StreamMessage>(){			
					public StreamMessage load(Long messageId) throws Exception { 
						return streamsDao.getStreamMessageById(messageId);
				}}
			);
	}

	private void fireEvent(Object event) {
		if( communitySpringEventPublisher!= null)
			communitySpringEventPublisher.fireEvent(event);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Streams createStreams(String name, String displayName, String description) { 
		DefaultStreams newStreams = new DefaultStreams();		 
		if(name == null || "".equals(name.trim()))
            throw new IllegalArgumentException("Streams name must be specified."); 
		newStreams.setName(name); 
		if(displayName == null || "".equals(displayName.trim()))
            throw new IllegalArgumentException("Streams display name must be specified.");
        
        newStreams.setDisplayName(displayName);        
        newStreams.setDescription(description);
        newStreams.setCreationDate(new Date());
        streamsDao.createStreams(newStreams);
		return newStreams;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateStreams(Streams streams) {
		streamsDao.saveOrUpdate(streams);
		streamsCache.invalidate(streams.getStreamId());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteStreams(Streams streams) {
		streamsDao.deleteStream(streams);
		streamsCache.invalidate(streams.getStreamId());
	}
 
	public Streams getStreamsById(long streamId) throws StreamsNotFoundException { 
		if (streamId < 1)
			throw new StreamsNotFoundException(); 
		Streams streams = null;
		try {
			streams = streamsCache.getIfPresent(streamId);
			if (streams == null) {
				streams = streamsCache.get(streamId);
			}
		} catch (ExecutionException e) {
			throw new StreamsNotFoundException(e);
		}
		return streams;
	} 
	
	public Streams getStreamsByName(String name) throws StreamsNotFoundException {
		try {
			Long id = streamsDao.getStreamIdByName(name);
			return getStreamsById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new StreamsNotFoundException(e);
		}
	}	
 
	public List<Streams> getAllStreams() {
		List<Long> ids = streamsDao.getAllStreamIds();
		List<Streams> list = new ArrayList<Streams>(ids.size());
		for( Long id : ids )
		{
			try {
				list.add(getStreamsById(id));
			} catch (StreamsNotFoundException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		return list;
	}
 
	public boolean streamsExist(long streamId) {
		if( streamId > 0L ) {
			try {
				getStreamsById( streamId );
				return true;
			} catch (StreamsNotFoundException e) {
			}
		}
		return false;
	} 

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addThread(int containerType, long containerId, StreamThread thread) { 
		DefaultStreamThread threadToUse = (DefaultStreamThread)thread; 
		DefaultStreamMessage rootMessage = (DefaultStreamMessage)threadToUse.getRootMessage();		
		boolean isNew = rootMessage.getThreadId() < 1L ;
		if( !isNew ){
			// get exist old value..
			
		} 
		// 1. insert thread ..
		streamsDao.createStreamThread(threadToUse);
		// 2. insert message ..
		streamsDao.createStreamMessage(threadToUse, rootMessage, -1L );	
		fireEvent(new StreamThreadEvent(threadToUse, StreamThreadEvent.Type.CREATED));	
	}
	

	public MessageTreeWalker getTreeWalker(StreamThread thread) {
		MessageTreeWalker walker = streamsDao.getTreeWalker(thread);
		return walker; 
	}
	
	public StreamThread createThread(int containerType, long containerId, StreamMessage rootMessage) {
		DefaultStreamThread newThread = new DefaultStreamThread(containerType, containerId, rootMessage);
		return newThread;
	}
	
	public StreamMessage createMessage(int containerType, long containerId) {
		DefaultStreamMessage newMessage = new DefaultStreamMessage(containerType, containerId, SecurityHelper.ANONYMOUS);
		return newMessage;
	}
 
	public StreamMessage createMessage(int containerType, long containerId, User user) {
		DefaultStreamMessage newMessage = new DefaultStreamMessage(containerType, containerId, user);
		return newMessage;
	}
 
	public int getStreamThreadCount(int objectType, long objectId) {
		return streamsDao.getStreamThreadCount(objectType, objectId);
	}
 
	public int getStreamThreadCount(Streams board) {
		return streamsDao.getStreamThreadCount( Models.STREAMS.getObjectType(), board.getStreamId() );
	}
 
	public List<StreamThread> getStreamThreads(int containerType, long containerId) {
		List<Long> threadIds = streamsDao.getStreamThreadIds(containerType, containerId);
		List<StreamThread> list = new ArrayList<StreamThread>(threadIds.size());
		for( Long threadId : threadIds )
		{
			try {
				list.add(getStreamThread(threadId));
			} catch (StreamThreadNotFoundException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		return list;
	}
 
	public List<StreamThread> getStreamThreads(int objectType, long objectId, int startIndex, int numResults) {
		List<Long> threadIds = streamsDao.getStreamThreadIds(objectType, objectId, startIndex, numResults);
		List<StreamThread> list = new ArrayList<StreamThread>(threadIds.size());
		for( Long threadId : threadIds )
		{
			try {
				list.add(getStreamThread(threadId));
			} catch (StreamThreadNotFoundException e) { 
				logger.warn(e.getMessage(), e);
			}
		}
		return list;
	}
 
	public List<StreamThread> getStreamMessages(int containerType, long containerId) { 
		return null;
	}
 
	public StreamThread getStreamThread(long threadId) throws StreamThreadNotFoundException { 
		
		if(threadId < 0L)
            throw new StreamThreadNotFoundException(CommunityLogLocalizer.format("013004", threadId )); 
		
		StreamThread threadToUse = threadCache.getIfPresent(threadId);
		
		if( threadToUse == null){ 
			try {
				threadToUse = streamsDao.getStreamThreadById(threadId);
				threadToUse.setLatestMessage(new DefaultStreamMessage(streamsDao.getLatestMessageId(threadToUse)));	
				((DefaultStreamThread)threadToUse).setMessageCount(streamsDao.getAllMessageIdsInThread(threadToUse).size());
			} catch (Exception e) {
				throw new StreamThreadNotFoundException(CommunityLogLocalizer.format("013005", threadId ), e);
			}			
			try {
				StreamMessage rootMessage = threadToUse.getRootMessage();		
				StreamMessage latestMessage = threadToUse.getLatestMessage();					
				threadToUse.setRootMessage(getStreamMessage(rootMessage.getMessageId()));					
				if(latestMessage != null && latestMessage.getMessageId() > 0){
					threadToUse.setLatestMessage(getStreamMessage(latestMessage.getMessageId()));	
					threadToUse.setModifiedDate(threadToUse.getLatestMessage().getModifiedDate());
				}						
			} catch (Exception e) {
				throw new StreamThreadNotFoundException(CommunityLogLocalizer.format("013005", threadId ), e);
			}
			threadCache.put(threadId, threadToUse);
		}
		return threadToUse;
	}
 
	public StreamMessage getStreamMessage(long messageId) throws StreamMessageNotFoundException {
		if(messageId < 0L)
			throw new StreamMessageNotFoundException(CommunityLogLocalizer.format("013006", messageId )); 
		StreamMessage messageToUse = messageCache.getIfPresent(messageId);
		if( messageToUse == null){
			try {
				messageToUse = streamsDao.getStreamMessageById(messageId);			
				if( messageToUse.getUser().getUserId() > 0){
					((DefaultStreamMessage)messageToUse).setUser(userManager.getUser(messageToUse.getUser()));
				}	
			} catch (Exception e) {
				throw new StreamMessageNotFoundException(CommunityLogLocalizer.format("013007", messageId ));
			}
			messageCache.put(messageToUse.getMessageId(), messageToUse);
		}
		return messageToUse;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateThread(StreamThread thread) {
		streamsDao.updateStreamThread(thread);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateMessage(StreamMessage message) { 
		streamsDao.updateStreamMessage(message);		
		try {
			StreamThread thread = getStreamThread(message.getThreadId());
			streamsDao.updateModifiedDate(thread, message.getModifiedDate() );  
			
			threadCache.invalidate(thread.getThreadId());
			messageCache.invalidate(message.getMessageId());
		} catch (StreamThreadNotFoundException e) {
			logger.error(e.getMessage(), e );
		}	
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addMessage(StreamThread thread, StreamMessage parentMessage, StreamMessage newMessage) { 
		DefaultStreamMessage newMessageToUse = (DefaultStreamMessage)newMessage;
		if(newMessageToUse.getCreationDate().getTime() < parentMessage.getCreationDate().getTime() ){
			logger.warn(CommunityLogLocalizer.getMessage("013008"));			
			Date newDate = new Date(parentMessage.getCreationDate().getTime() + 1L);
			newMessageToUse.setCreationDate(newDate);
			newMessageToUse.setModifiedDate(newDate);
		}		
		if(thread.getThreadId() != -1L){
			newMessageToUse.setThreadId(thread.getThreadId());
		}		
		streamsDao.createStreamMessage(thread, newMessageToUse, parentMessage.getMessageId());
		updateThreadModifiedDate(thread, newMessageToUse);		 		
	}
 
	protected void updateThreadModifiedDate(StreamThread thread, StreamMessage message){
		if( message.getModifiedDate() != null){			
			thread.setModifiedDate(message.getModifiedDate());
			
			threadCache.invalidate(thread.getThreadId());
			messageCache.invalidate(message.getMessageId());
		}
	}
	
	public int getMessageCount(StreamThread thread) {
		return streamsDao.getMessageCount(thread);
	}
 
	public List<StreamMessage> getMessages(StreamThread thread) {
		List<Long> messageIds = streamsDao.getMessageIds(thread);
		List<StreamMessage> list = new ArrayList<StreamMessage>(messageIds.size());
		for(Long messageId : messageIds){
			try {
				list.add(getStreamMessage(messageId));
			} catch (StreamMessageNotFoundException e) {
			}
		}
		return list;
	}

	public void deleteThread(StreamThread thread) {
		streamsDao.deleteThread(thread);
	}

	@Override
	public void deleteMessage(StreamThread thread, StreamMessage message, boolean recursive) {

	}


}
