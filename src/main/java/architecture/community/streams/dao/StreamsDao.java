package architecture.community.streams.dao;

import java.util.Date;
import java.util.List;

import architecture.community.streams.MessageTreeWalker;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamThread;
import architecture.community.streams.Streams;
 

public interface StreamsDao {

	public abstract void createStreams(Streams board);
	
	public abstract Streams getStreamById(long boardId);
	
	public abstract Long getStreamIdByName(String name);
	
	public abstract List<Long> getAllStreamIds();
	
	public void deleteStream(Streams board);
	
	public void saveOrUpdate( Streams board );
	

	public abstract void createStreamThread( StreamThread thread );
	
	public abstract void createStreamMessage (StreamThread thread, StreamMessage message, long parentMessageId);
	
	public abstract int getStreamThreadCount(int objectType, long objectId);	
	
	public abstract List<Long> getStreamThreadIds(int objectType, long objectId);
	
	public abstract List<Long> getStreamThreadIds(int objectType, long objectId, int startIndex, int numResults);
	
	public abstract long getLatestMessageId(StreamThread thread);
	
	public abstract List<Long> getAllMessageIdsInThread(StreamThread thread);
	
	public abstract StreamThread getStreamThreadById(long threadId);
		
	public abstract int getStreamMessageCount(int objectType, long objectId);	
	
	public abstract StreamMessage getStreamMessageById(long messageId) ;
	
	public abstract void updateStreamMessage(StreamMessage message);
	
	public abstract void updateStreamThread(StreamThread thread);
	
	public abstract void updateModifiedDate(StreamThread thread, Date date);
	
	public abstract List<Long> getMessageIds(StreamThread thread);
	
	public abstract int getMessageCount(StreamThread thread);	
	
	public abstract MessageTreeWalker getTreeWalker(StreamThread thread) ;
	
	public abstract void updateParentMessage( long messageId, long newParentId );
	
	public abstract void deleteMessage(StreamMessage message);
	
	public void deleteThread(StreamThread thread);
	
}
