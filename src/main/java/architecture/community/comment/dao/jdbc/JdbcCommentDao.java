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

package architecture.community.comment.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.comment.Comment;
import architecture.community.comment.DefaultComment;
import architecture.community.comment.dao.CommentDao;
import architecture.community.model.ModelObjectTreeWalker;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.community.util.LongTree;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcCommentDao extends ExtendedJdbcDaoSupport implements CommentDao {

	protected static final RowMapper<Comment> commentMapper = new RowMapper<Comment>() {
		public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			DefaultComment comment = new DefaultComment(rs.getLong("COMMENT_ID"));			
			long parentCommentId = rs.getLong("PARENT_COMMENT_ID");			
			if (!rs.wasNull())
				comment.setParentCommentId(parentCommentId);
			comment.setObjectType(rs.getInt("OBJECT_TYPE"));
			comment.setObjectId(rs.getLong("OBJECT_ID"));			
			int objectType = rs.getInt("PARENT_OBJECT_TYPE");			
			if (!rs.wasNull())
				comment.setParentObjectType(objectType);
			
			long objectID = rs.getLong("PARENT_OBJECT_ID");
			if (!rs.wasNull())
				comment.setParentObjectId(objectID);
			
			long userId = rs.getLong("USER_ID");			
			if (!rs.wasNull())
				comment.setUser(new UserTemplate(userId));			
			comment.setName(rs.getString("NAME"));
			comment.setEmail(rs.getString("EMAIL"));
			comment.setURL(rs.getString("URL"));
			comment.setIPAddress(rs.getString("IP"));			
			String dbText = rs.getString("BODY");			
			comment.setBody(dbText);
			comment.setCreationDate(rs.getTimestamp("CREATION_DATE"));
			comment.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
			comment.setStatus(Comment.Status.valueOf(Integer.valueOf(rs.getInt("STATUS"))));
			return comment;
		}
	};
	    
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
	
	public JdbcCommentDao() {
	}

	public long getNextCommentId(){		
		return sequencerFactory.getNextValue(Models.COMMENT.getObjectType(), Models.COMMENT.name());
	}
	
	
	public Comment getCommentById(long commentId) {
		return getExtendedJdbcTemplate().queryForObject(
			    getBoundSql("COMMUNITY_WEB.SELECT_COMMENT_BY_ID").getSql(), commentMapper,
			    new SqlParameterValue(Types.NUMERIC, commentId));		   
    }

	public void deleteComment(Comment comment) {
		getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_COMMENT").getSql(),
				new SqlParameterValue(Types.NUMERIC, comment.getCommentId()));
	}
	 
	public void createComment(Comment comment) {
		if (comment.getCommentId() == -1L)
		    ((DefaultComment)comment).setCommentId(getNextCommentId());
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_COMMENT").getSql(),
			new SqlParameterValue(Types.NUMERIC, comment.getCommentId()),
			new SqlParameterValue(Types.NUMERIC, comment.getObjectType()),
			new SqlParameterValue(Types.NUMERIC, comment.getObjectId()),
			new SqlParameterValue(Types.NUMERIC, comment.getParentCommentId()),
			new SqlParameterValue(Types.NUMERIC, comment.getParentObjectType()),
			new SqlParameterValue(Types.NUMERIC, comment.getParentObjectId()),
			new SqlParameterValue(Types.NUMERIC, comment.getUser().getUserId()),
			new SqlParameterValue(Types.VARCHAR, comment.getName()),
			new SqlParameterValue(Types.VARCHAR, comment.getEmail()),
			new SqlParameterValue(Types.VARCHAR, comment.getURL()),
			new SqlParameterValue(Types.VARCHAR, comment.getIPAddress()),
			new SqlParameterValue(Types.VARCHAR, comment.getBody()),
			new SqlParameterValue(Types.VARCHAR, comment.getStatus().getIntValue()),
			new SqlParameterValue(Types.TIMESTAMP, comment.getCreationDate()),
			new SqlParameterValue(Types.TIMESTAMP, comment.getModifiedDate()));		
	}
 
	public void updateComment(Comment comment) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_COMMENT").getSql(),
				new SqlParameterValue(Types.NUMERIC, comment.getParentCommentId()),
				new SqlParameterValue(Types.NUMERIC, comment.getName()),
				new SqlParameterValue(Types.NUMERIC, comment.getEmail()),
				new SqlParameterValue(Types.NUMERIC, comment.getURL()),
				new SqlParameterValue(Types.VARCHAR, comment.getIPAddress()),
				new SqlParameterValue(Types.VARCHAR, comment.getBody()),
				new SqlParameterValue(Types.VARCHAR, comment.getStatus().getIntValue()),
				new SqlParameterValue(Types.TIMESTAMP, comment.getModifiedDate()));
	}
 
	public ModelObjectTreeWalker getTreeWalker(int objectType, long objectId) {

		int numComments = getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.COUNT_COMMENT_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));

		numComments++;

		final LongTree tree = new LongTree(-1L, numComments);

		getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_WEB.SELECT_ROOT_COMMENT").getSql(),
				new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						long commentId = rs.getLong(1);
						tree.addChild(-1L, commentId);
					}
				}, new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));

		getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_WEB.SELECT_CHILD_COMMENT").getSql(),
				new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						long commentId = rs.getLong(1);
						long parentCommentId = rs.getLong(2);
						tree.addChild(parentCommentId, commentId);
					}
				}, new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));
		
		return new ModelObjectTreeWalker(objectType, objectId, tree);
	}

}
