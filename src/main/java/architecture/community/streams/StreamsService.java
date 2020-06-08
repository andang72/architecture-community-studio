package architecture.community.streams;

import java.util.List;

import architecture.community.user.User;

public interface StreamsService {

	public abstract Streams createStreams(String name, String displayName, String description);
	
	public abstract void updateStreams(Streams streams);

	public abstract void deleteStreams(Streams streams);
	
	public abstract Streams getStreamsById(long streamId) throws StreamsNotFoundException ;
	
	public abstract Streams getStreamsByName(String name) throws StreamsNotFoundException ;
	
	public abstract List<Streams> getAllStreams();	
	
	public abstract boolean streamsExist (long streamsId);
	
	
	/**
	 * containerType, containerId 에 해당하는 새로운 토픽을 생성한다.
	 * 
	 * @param containerType
	 * @param containerId
	 * @param rootMessage
	 * @return
	 */
	public abstract StreamThread createThread(int containerType, long containerId, StreamMessage rootMessage);
	
	/**
	 * containerType, containerId 에 새로운 토픽을 추가한다.
	 * 
	 * @param containerType
	 * @param containerId
	 * @param thread
	 */
	public abstract void addThread(int containerType, long containerId, StreamThread thread);
	
	/**
	 * containerType, containerId 에 등록된 모든 토픽 수를 리턴한다.
	 * 
	 * @param containerType
	 * @param containerId
	 * @return
	 */
	public abstract int getStreamThreadCount(int objectType, long objectId);		
	
	public abstract int getStreamThreadCount(Streams board) ; 
	
	
	
	
	
 	public abstract StreamMessage createMessage(int containerType, long containerId);
	
	public abstract StreamMessage createMessage(int containerType, long containerId, User user);	
	
	
	public abstract List<StreamThread> getStreamThreads(int containerType, long containerId);
	
	public abstract List<StreamThread> getStreamThreads(int objectType, long objectId, int startIndex, int numResults);
	
	public abstract List<StreamThread> getStreamMessages(int containerType, long containerId);
			
	public abstract StreamThread getStreamThread(long threadId) throws StreamThreadNotFoundException ;
	
	public abstract StreamMessage getStreamMessage(long messageId) throws StreamMessageNotFoundException ;
	
	public abstract void updateThread(StreamThread thread);
		
	public abstract void updateMessage(StreamMessage message);
	
	public abstract void addMessage(StreamThread forumthread, StreamMessage parentMessage, StreamMessage newMessage);
	
	public abstract int getMessageCount(StreamThread thread);
	
	public abstract List<StreamMessage> getMessages(StreamThread thread);
	
	public abstract MessageTreeWalker getTreeWalker(StreamThread thread);	
	
	public abstract void deleteThread(StreamThread thread) ;
			
	public abstract void deleteMessage(StreamThread thread, StreamMessage message, boolean recursive);
	
}
