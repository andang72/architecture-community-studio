/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.attachment.dao.jdbc;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.DefaultAttachment;
import architecture.community.attachment.dao.AttachmentDao;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.spring.jdbc.InputStreamRowMapper;

public class JdbcAttachmentDao extends ExtendedJdbcDaoSupport implements AttachmentDao {

    
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	
	private String propertyTableName = "AC_UI_ATTACHMENT_PROPERTY";
	private String propertyPrimaryColumnName = "ATTACHMENT_ID";

	private boolean enableSaveUserId = false ;
	
	private final RowMapper<Attachment> attachmentMapper = new RowMapper<Attachment>(){
		public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultAttachment image = new DefaultAttachment();			
			if(enableSaveUserId){
				image.setUser(new UserTemplate( rs.getLong("USER_ID") ) );
			}
			image.setAttachmentId(rs.getLong("ATTACHMENT_ID"));
			image.setObjectType(rs.getInt("OBJECT_TYPE"));
			image.setObjectId(rs.getLong("OBJECT_ID"));
			
			image.setName(rs.getString("FILE_NAME"));
			
			String normalized = java.text.Normalizer.normalize( image.getName(), Normalizer.Form.NFC );
			image.setName(normalized);
			
			image.setSize(rs.getInt("FILE_SIZE"));
			image.setContentType(rs.getString("CONTENT_TYPE"));
			image.setCreationDate(rs.getDate("CREATION_DATE"));
			image.setModifiedDate(rs.getDate("MODIFIED_DATE"));			
			return image;
		}		
	};
	
	

	public Map<String, String> getAttachmentProperties(long attachmentId) {
		return propertyDao.getProperties(propertyTableName, propertyPrimaryColumnName, attachmentId);
	}

	public void deleteAttachmentProperties(long attachmentId) {
		propertyDao.deleteProperties(propertyTableName, propertyPrimaryColumnName, attachmentId);
	}
	
	public void setAttachmentProperties(long attachmentId, Map<String, String> props) {
		propertyDao.updateProperties(propertyTableName, propertyPrimaryColumnName, attachmentId, props);
	}
	
	public void insertAttachmentDownloads(List<AttachmentDownloadItem> list) {
		
		final List<AttachmentDownloadItem> itemsToUse = list;		
		getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_WEB.INSERT_ATTACHMENT_DOWNLOAD" ).getSql(),
			    new BatchPreparedStatementSetter(){
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						for( AttachmentDownloadItem item : itemsToUse ){
							long time = System.currentTimeMillis();
                            ps.setLong( 1, item.getAttachmentId() );                  
                            ps.setDate( 2, new java.sql.Date( item.getDownloadDate().getTime() ));
                            ps.setInt( 3,  item.isDownloadCompleted() ? 1 : 0  );
						}
					}
					public int getBatchSize() {
						return itemsToUse.size();
					}});				
	}


	public long getNextAttachmentId(){
		return sequencerFactory.getNextValue(Models.ATTACHMENT.getObjectType(), Models.ATTACHMENT.name());
	}	
	
	
	public boolean isEnableSaveUserId() {
		return enableSaveUserId;
	}

	public void setEnableSaveUserId(boolean enableSaveUserId) {
		this.enableSaveUserId = enableSaveUserId;
	}

	public List<Long> getAllAttachmentIds() {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_WEB.SELECT_ATTACHMENT_IDS" ).getSql(),
				new RowMapper<Long>(){
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getLong(1);
					}}
		);
	}

	public void deleteAttachmentData() {
		getJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_ALL_ATTACHMENT_DATAS" ).getSql()		
		);
	}

	public Attachment createAttachment(Attachment attachment) {
		
		Attachment toUse = attachment;		
		if( toUse.getAttachmentId() <1L){
			long attachmentId = getNextAttachmentId();
			toUse.setAttachmentId(attachmentId);
		}
		
		if( enableSaveUserId ){
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_ATTACHMENT").getSql(), 	
					new SqlParameterValue (Types.NUMERIC, toUse.getAttachmentId()), 
					new SqlParameterValue (Types.INTEGER, toUse.getObjectType() ), 
					new SqlParameterValue (Types.NUMERIC, toUse.getObjectId() ), 
					new SqlParameterValue (Types.VARCHAR, toUse.getContentType()), 
					new SqlParameterValue (Types.VARCHAR, toUse.getName() ), 
					new SqlParameterValue (Types.INTEGER, toUse.getSize() ), 	
					
					new SqlParameterValue (Types.NUMERIC, toUse.getUser().getUserId() ), 
					
					new SqlParameterValue(Types.DATE, toUse.getCreationDate()),
					new SqlParameterValue(Types.DATE, toUse.getModifiedDate()));				
		}else{
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_ATTACHMENT").getSql(), 	
					new SqlParameterValue (Types.NUMERIC, toUse.getAttachmentId()), 
					new SqlParameterValue (Types.INTEGER, toUse.getObjectType() ), 
					new SqlParameterValue (Types.NUMERIC, toUse.getObjectId() ), 
					new SqlParameterValue (Types.VARCHAR, toUse.getContentType()), 
					new SqlParameterValue (Types.VARCHAR, toUse.getName() ), 
					new SqlParameterValue (Types.INTEGER, toUse.getSize() ), 	
					new SqlParameterValue (Types.NUMERIC, toUse.getUser().getUserId() ), 
					new SqlParameterValue(Types.DATE, toUse.getCreationDate()),
					new SqlParameterValue(Types.DATE, toUse.getModifiedDate()));			
		}		
		return toUse;
		
	}

	public void updateAttachment(Attachment attachment) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_ATTACHMENT").getSql(), 	
				new SqlParameterValue (Types.INTEGER, attachment.getObjectType() ), 
				new SqlParameterValue (Types.NUMERIC, attachment.getObjectId() ), 
				new SqlParameterValue (Types.VARCHAR, attachment.getContentType()), 
				new SqlParameterValue (Types.VARCHAR, attachment.getName() ), 
				new SqlParameterValue (Types.INTEGER, attachment.getSize() ), 
				new SqlParameterValue(Types.DATE, attachment.getCreationDate()),
				new SqlParameterValue(Types.DATE, attachment.getModifiedDate()),
				new SqlParameterValue (Types.NUMERIC, attachment.getAttachmentId()) );		
		setAttachmentProperties(attachment.getAttachmentId(), attachment.getProperties());	
	}

	public void deleteAttachment(Attachment attachment) {		
		getJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_ATTACHMENT" ).getSql(),
				new SqlParameterValue (Types.NUMERIC, attachment.getAttachmentId())
		);
		deleteAttachmentProperties(attachment.getAttachmentId());
	}

	public void deleteAttachmentData(Attachment attachment) {		
		getJdbcTemplate().update(
			getBoundSql("COMMUNITY_WEB.DELETE_ATTACHMENT_DATA_BY_ID" ).getSql(),
			new SqlParameterValue (Types.NUMERIC, attachment.getAttachmentId())
		);
	}
	
	public InputStream getAttachmentData(Attachment attachment) {		
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.SELECT_ATTACHMENT_DATA_BY_ID").getSql(), new InputStreamRowMapper(), new SqlParameterValue (Types.NUMERIC, attachment.getAttachmentId()));		
	}

	public void saveAttachmentData(Attachment attachment, InputStream inputstream) {

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ATTACHMENT_DATA_BY_ID").getSql(), new SqlParameterValue (Types.NUMERIC, attachment.getAttachmentId()));		
		if( getExtendedJdbcTemplate().getDBInfo() == DB.ORACLE ){									
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_EMPTY_ATTACHMENT_DATA").getSql(), new SqlParameterValue (Types.NUMERIC, attachment.getAttachmentId()));
			getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.UPDATE_ATTACHMENT_DATA").getSql(), 
					new Object[]{
						new SqlLobValue( inputstream , attachment.getSize(), getLobHandler()),
						attachment.getAttachmentId()
				}, 
				new int[]{
					Types.BLOB,
					Types.NUMERIC
				});
		}else{			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_ATTACHMENT_DATA").getSql(), 
				new SqlParameterValue ( Types.NUMERIC, attachment.getAttachmentId()), 
				new SqlParameterValue ( Types.BLOB,  new SqlLobValue( inputstream , attachment.getSize(), getLobHandler() ) ) 
			);
		}
	}

	public Attachment getByAttachmentId(long attachmentId) {
		
		Attachment attachment = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.SELECT_ATTACHMENT_BY_ID").getSql(), attachmentMapper, new SqlParameterValue (Types.NUMERIC, attachmentId ));		
		attachment.setProperties( getAttachmentProperties(attachmentId) );
		return attachment;
	}

	public List<Attachment> getByObjectTypeAndObjectId(int objectType, long objectId) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_WEB.SELECT_ATTACHMENT_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				attachmentMapper,
				new SqlParameterValue (Types.NUMERIC, objectType ), new SqlParameterValue (Types.NUMERIC, objectId ));		
	}

	public int getAttachmentCount(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.COUNT_ATTACHMENT_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType ), 
				new SqlParameterValue(Types.NUMERIC, objectId ));
	}
	
	public List<Long> getAttachmentIds(int objectType, long objectId) {		
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_WEB.SELECT_ATTACHMENT_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
				Long.class,
				new SqlParameterValue (Types.INTEGER, objectType), 
				new SqlParameterValue (Types.NUMERIC, objectId )
			);
	}

 
	public List<Long> getAttachmentIds(int objectType, long objectId, int startIndex, int maxResults) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_WEB.SELECT_ATTACHMENT_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				startIndex, 
				maxResults, 
				Long.class, 
				new SqlParameterValue(Types.NUMERIC, objectType ),
				new SqlParameterValue(Types.NUMERIC, objectId )
		);
	}
	

	public void move(int objectType, long objectId, int targetObjectType, long targetObjectId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.MOVE_ATTACHMENTS").getSql(), 	
				new SqlParameterValue (Types.INTEGER, targetObjectType ), 
				new SqlParameterValue (Types.NUMERIC, targetObjectId ),
				new SqlParameterValue (Types.INTEGER, objectType ), 
				new SqlParameterValue (Types.NUMERIC, objectId )
				);
	}


}
