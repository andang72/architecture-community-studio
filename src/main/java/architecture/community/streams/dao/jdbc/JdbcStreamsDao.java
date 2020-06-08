package architecture.community.streams.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.model.Models;
import architecture.community.streams.DefaultStreamMessage;
import architecture.community.streams.DefaultStreamThread;
import architecture.community.streams.DefaultStreams;
import architecture.community.streams.MessageTreeWalker;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamThread;
import architecture.community.streams.Streams;
import architecture.community.streams.dao.StreamsDao;
import architecture.community.user.UserTemplate;
import architecture.community.util.LongTree;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.util.StringUtils;

public class JdbcStreamsDao extends ExtendedJdbcDaoSupport implements StreamsDao {

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;
	
	private String streamsPropertyTableName = "AC_UI_STREAMS_PROPERTY";
	private String streamsPropertyPrimaryColumnName = "STREAM_ID";
	
	private final RowMapper<Streams> streamsMapper = new RowMapper<Streams>() {		
		public Streams mapRow(ResultSet rs, int rowNum) throws SQLException {			
			DefaultStreams streams = new DefaultStreams(rs.getLong("STREAM_ID"));	
			streams.setName(rs.getString("NAME"));
			streams.setCategoryId(rs.getLong("CATEGORY_ID"));
			streams.setDisplayName(rs.getString("DISPLAY_NAME"));
			streams.setDescription(rs.getString("DESCRIPTION"));
			streams.setCreationDate(rs.getDate("CREATION_DATE"));
			streams.setModifiedDate(rs.getDate("MODIFIED_DATE"));		
			return streams;
		}		
	};

	
	private final RowMapper<StreamThread> threadMapper = new RowMapper<StreamThread>() {	
		
		public StreamThread mapRow(ResultSet rs, int rowNum) throws SQLException {			
			DefaultStreamThread thread = new DefaultStreamThread(rs.getLong("THREAD_ID"));			
			thread.setObjectType(rs.getInt("OBJECT_TYPE"));
			thread.setObjectId(rs.getLong("OBJECT_ID"));
			thread.setRootMessage(  new DefaultStreamMessage( rs.getLong("ROOT_MESSAGE_ID") ) );
			thread.setCreationDate(rs.getTimestamp("CREATION_DATE"));
			thread.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));		
			return thread;
		}
		
	};

	private final RowMapper<StreamMessage> messageMapper = new RowMapper<StreamMessage>() {	 
		public StreamMessage mapRow(ResultSet rs, int rowNum) throws SQLException {			
			DefaultStreamMessage message = new DefaultStreamMessage(rs.getLong("MESSAGE_ID"));		
			message.setParentMessageId(rs.getLong("PARENT_MESSAGE_ID"));
			message.setThreadId(rs.getLong("THREAD_ID"));
			message.setObjectType(rs.getInt("OBJECT_TYPE"));
			message.setObjectId(rs.getLong("OBJECT_ID"));
			message.setUser(new UserTemplate(rs.getLong("USER_ID")));
			message.setSubject(rs.getString("SUBJECT"));
			message.setBody(rs.getString("BODY"));
			message.setKeywords(rs.getString("KEYWORDS"));
			message.setCreationDate(rs.getTimestamp("CREATION_DATE"));
			message.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));		
			return message;
		}
		
	};
	
	
	public JdbcStreamsDao() { 
	}
	
	protected long getNextStreamsId(){
		logger.debug("next id for {}, {}", Models.STREAMS.getObjectType(), Models.STREAMS.name() );
		return sequencerFactory.getNextValue(Models.STREAMS.getObjectType(), Models.STREAMS.name());
	}
	
	protected long getNextThreadId(){
		logger.debug("next id for {}, {}", Models.STREAMS_THREAD.getObjectType(), Models.STREAMS_THREAD.name() );
		return sequencerFactory.getNextValue(Models.STREAMS_THREAD.getObjectType(), Models.STREAMS_THREAD.name());
	}
	
	protected long getNextMessageId(){
		logger.debug("next id for {}, {}", Models.STREAMS_MESSAGE.getObjectType(), Models.STREAMS_MESSAGE.name() );
		return sequencerFactory.getNextValue(Models.STREAMS_MESSAGE.getObjectType(), Models.STREAMS_MESSAGE.name());
	}
	

	public void setStreamsPropertyTableName(String streamsPropertyTableName) {
		this.streamsPropertyTableName = streamsPropertyTableName;
	}

	public void setStreamsPropertyPrimaryColumnName(String streamsPropertyPrimaryColumnName) {
		this.streamsPropertyPrimaryColumnName = streamsPropertyPrimaryColumnName;
	}
 
	public void createStreams(Streams board) {
		if( board == null)
			throw new IllegalArgumentException();
		
		if( board.getStreamId() <= 0 ){
			((DefaultStreams)board).setStreamId(getNextStreamsId());
		}		
		Date now = new Date();		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.CREATE_STREAMS").getSql(),
				new SqlParameterValue(Types.NUMERIC, board.getCategoryId()),
				new SqlParameterValue(Types.NUMERIC, board.getStreamId()),
				new SqlParameterValue(Types.VARCHAR, board.getName()),
				new SqlParameterValue(Types.VARCHAR, StringUtils.isNullOrEmpty(board.getDisplayName()) ? board.getName() : board.getDisplayName()),
				new SqlParameterValue(Types.VARCHAR, board.getDescription()),
				new SqlParameterValue(Types.TIMESTAMP, board.getCreationDate() != null ? board.getCreationDate() : now ),
				new SqlParameterValue(Types.TIMESTAMP, board.getModifiedDate() != null ? board.getModifiedDate() : now )
		);
		setStreamsProperties(board.getStreamId(), board.getProperties());	
	}

	public Map<String, String> getStreamsProperties(long boardId) {
		return propertyDao.getProperties(streamsPropertyTableName, streamsPropertyPrimaryColumnName, boardId);
	}

	public void deleteStreamsProperties(long streamId) {
		propertyDao.deleteProperties(streamsPropertyTableName, streamsPropertyPrimaryColumnName, streamId);
	}
	
	public void setStreamsProperties(long streamId, Map<String, String> props) {
		propertyDao.updateProperties(streamsPropertyTableName, streamsPropertyPrimaryColumnName, streamId, props);
	}  
	 
	public Streams getStreamById(long streamId) {
		if (streamId <= 0L) {
			return null;
		}		
		Streams streams = null;
		try {
			streams = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_STREAMS.SELECT_STREAMS_BY_ID").getSql(),  streamsMapper,  new SqlParameterValue(Types.NUMERIC, streamId));
			streams.setProperties(getStreamsProperties(streams.getStreamId()));
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() > 1) {
				logger.warn(CommunityLogLocalizer.format("013002", streamId));
				throw e;
			}
		} catch (DataAccessException e) {
			logger.error(CommunityLogLocalizer.format("013001", streamId), e);
		}
		return streams;
	}
	
 
	public Long getStreamIdByName(String name) {
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_STREAMS.SELECT_STREAMS_ID_BY_NAME").getSql(), Long.class, new SqlParameterValue(Types.VARCHAR, name));
	}
	
 
	public List<Long> getAllStreamIds() {
		return getExtendedJdbcTemplate().queryForList(getBoundSql("COMMUNITY_STREAMS.SELECT_ALL_STREAMS_IDS").getSql(), Long.class);
	}
 
	public void deleteStream(Streams stream) { 
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.DELETE_STREAMS").getSql(), new SqlParameterValue(Types.NUMERIC, stream.getStreamId() ) );
	}
 
	public void saveOrUpdate(Streams stream) { 
		if( stream.getStreamId() > 0 ) {
			// update 	
			Date now = new Date();
			stream.setModifiedDate(now);		
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.UPDATE_STREAMS").getSql(), 
					new SqlParameterValue(Types.VARCHAR, 	stream.getName() ),
					new SqlParameterValue(Types.VARCHAR, 	StringUtils.isNullOrEmpty(stream.getDisplayName()) ? stream.getName() : stream.getDisplayName()),
					new SqlParameterValue(Types.VARCHAR, 	stream.getDescription()),
					new SqlParameterValue(Types.TIMESTAMP, 	stream.getModifiedDate() ),	
					new SqlParameterValue(Types.NUMERIC, 	stream.getStreamId() )
			);
			if(!stream.getProperties().isEmpty())
				setStreamsProperties(stream.getStreamId(), stream.getProperties());
			
		}else {
			createStreams(stream);
		}
	}
  
	@Override
	public void createStreamThread(StreamThread thread) {
		DefaultStreamThread threadToUse = (DefaultStreamThread)thread;		
		threadToUse.setThreadId(getNextThreadId()); 
		if( threadToUse.getRootMessage().getMessageId() <= 0 ){
			DefaultStreamMessage messageToUse = (DefaultStreamMessage) thread.getRootMessage();		
			messageToUse.setMessageId(getNextMessageId());		
		} 
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.CREATE_STREAM_THREAD").getSql(),
				new SqlParameterValue(Types.NUMERIC, threadToUse.getThreadId() ),
				new SqlParameterValue(Types.NUMERIC, threadToUse.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, threadToUse.getObjectId()),				
				new SqlParameterValue(Types.NUMERIC, threadToUse.getRootMessage().getMessageId()),				
				new SqlParameterValue(Types.TIMESTAMP, threadToUse.getCreationDate() ),
				new SqlParameterValue(Types.TIMESTAMP, threadToUse.getModifiedDate() )
		);
	}
 
	public void createStreamMessage(StreamThread thread, StreamMessage message, long parentMessageId) {
		DefaultStreamMessage messageToUse = (DefaultStreamMessage) message;
		if(messageToUse.getCreationDate() == null )
		{
			Date now = new Date();	
			messageToUse.setCreationDate(now);
			messageToUse.setModifiedDate(now);
		}	
		
		if(messageToUse.getMessageId() == -1L || messageToUse.getMessageId() == 0L){
			messageToUse.setMessageId(getNextMessageId());
		}
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.CREATE_STREAM_MESSAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, messageToUse.getMessageId() ),
				new SqlParameterValue(Types.NUMERIC, parentMessageId),
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId()),
				new SqlParameterValue(Types.NUMERIC, thread.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, thread.getObjectId()),
				new SqlParameterValue(Types.NUMERIC, messageToUse.getUser().getUserId()),
				new SqlParameterValue(Types.VARCHAR, messageToUse.getKeywords()),
				new SqlParameterValue(Types.VARCHAR, messageToUse.getSubject()),
				new SqlParameterValue(Types.VARCHAR, messageToUse.getBody()),
				new SqlParameterValue(Types.TIMESTAMP, messageToUse.getCreationDate() ),
				new SqlParameterValue(Types.TIMESTAMP, messageToUse.getModifiedDate() )
		);	
	}
 
	public int getStreamThreadCount(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_COUNT_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType ),
				new SqlParameterValue(Types.NUMERIC, objectId )
				);
	}
 
	public List<Long> getStreamThreadIds(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType ),
				new SqlParameterValue(Types.NUMERIC, objectId )
				);
	}
 
	public List<Long> getStreamThreadIds(int objectType, long objectId, int startIndex, int numResults) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				startIndex, 
				numResults, 
				Long.class, 
				new SqlParameterValue(Types.NUMERIC, objectType ),
				new SqlParameterValue(Types.NUMERIC, objectId )
		);
	}
 
	public long getLatestMessageId(StreamThread thread) {
		try {
			return getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_STREAMS.SELECT_LATEST_STREAM_MESSAGE_ID_BY_THREAD_ID").getSql(), Long.class,
					new SqlParameterValue(Types.NUMERIC, thread.getThreadId()));
		} catch (IncorrectResultSizeDataAccessException e) {
			logger.error(CommunityLogLocalizer.format("013009", thread.getThreadId()), e);
		}
		return -1L;
	}
 
	public List<Long> getAllMessageIdsInThread(StreamThread thread) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_STREAMS.SELECT_ALL_STREAM_MESSAGE_IDS_BY_THREAD_ID").getSql(), 
				Long.class,
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId() )
			);		
	}
 
	public StreamThread getStreamThreadById(long threadId) {
		StreamThread thread = null;
		if (threadId <= 0L) {
			return thread;
		}		
		try {
			thread = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_BY_ID").getSql(), 
					threadMapper, 
					new SqlParameterValue(Types.NUMERIC, threadId ));
		} catch (DataAccessException e) {
			logger.error(CommunityLogLocalizer.format("013005", threadId), e);
		}
		return thread;
	}
 
	public int getStreamMessageCount(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_MESSAGE_COUNT_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType),
				new SqlParameterValue(Types.NUMERIC, objectId)
		);
	}
 
	public StreamMessage getStreamMessageById(long messageId) {
		StreamMessage message = null;
		if (messageId <= 0L) {
			return message;
		}		
		try {
			message = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_MESSAGE_BY_ID").getSql(), 
					messageMapper, 
					new SqlParameterValue(Types.NUMERIC, messageId ));
		} catch (DataAccessException e) {
			logger.error(CommunityLogLocalizer.format("013007", messageId), e);
		}
		return message;
	}
 
	public void updateStreamMessage(StreamMessage message) {
		Date now = new Date();
		message.setModifiedDate(now);		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.UPDATE_STREAM_MESSAGE").getSql(), 
				new SqlParameterValue(Types.VARCHAR, message.getKeywords() ),
				new SqlParameterValue(Types.VARCHAR, message.getSubject() ),
				new SqlParameterValue(Types.VARCHAR, message.getBody()),
				new SqlParameterValue(Types.TIMESTAMP, message.getModifiedDate() ),	
				new SqlParameterValue(Types.NUMERIC, message.getMessageId() )
		);
	}
 
	public void updateStreamThread(StreamThread thread) {
		Date now = new Date();
		thread.setModifiedDate(now);		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.UPDATE_STREAM_THREAD").getSql(), 
				new SqlParameterValue(Types.VARCHAR, thread.getRootMessage().getMessageId()),
				new SqlParameterValue(Types.TIMESTAMP, thread.getModifiedDate() ),	
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId())
		);
	}
 
	public void updateModifiedDate(StreamThread thread, Date date) {
		thread.setModifiedDate(date);	
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.UPDATE_STREAM_THREAD_MODIFIED_DATE").getSql(), 
				new SqlParameterValue(Types.TIMESTAMP, date ),	
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId() )
		);
	}
 
	public List<Long> getMessageIds(StreamThread thread) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_MESSAGE_IDS_BY_THREAD_ID").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId() )
				);
	}

	@Override
	public int getMessageCount(StreamThread thread) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_MESSAGE_COUNT_BY_THREAD_ID").getSql(), 
				Integer.class,
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId() )
		);
	}
 
	public MessageTreeWalker getTreeWalker(StreamThread thread) { 
		final LongTree tree = new LongTree(thread.getRootMessage().getMessageId(), getMessageCount(thread));
		getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_STREAMS.SELECT_STREAM_THREAD_MESSAGES_BY_THREAD_ID").getSql(), 
				new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						long messageId = rs.getLong(1);
						long parentMessageId = rs.getLong(2);
						tree.addChild(parentMessageId, messageId);						
					}}, 
				new SqlParameterValue(Types.NUMERIC, thread.getThreadId() )); 
		return new MessageTreeWalker( thread.getThreadId(), tree);
	}
 
	public void updateParentMessage(long newParentId, long oldParentId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.UPDATE_PARENT_MESSAGE_ID").getSql(), 
				new SqlParameterValue(Types.NUMERIC, newParentId ),
				new SqlParameterValue(Types.NUMERIC, oldParentId)
		);
	}
 
	public void deleteMessage(StreamMessage message) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.DELETE_STREAM_MESSAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, message.getMessageId() ));	
		
	}
 
	public void deleteThread(StreamThread thread) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.DELETE_STREAM_MESSAGE_BY_THREAD_ID").getSql(),
			new SqlParameterValue(Types.NUMERIC, thread.getThreadId() ));
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_STREAMS.DELETE_STREAM_THREAD").getSql(),
			new SqlParameterValue(Types.NUMERIC, thread.getThreadId() ));		
		
	}

}
