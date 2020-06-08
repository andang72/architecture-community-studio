package architecture.community.share.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.share.SharedLink;
import architecture.community.share.dao.SharedLinkDao;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcSharedLinkDao extends ExtendedJdbcDaoSupport implements SharedLinkDao {


	private final RowMapper<SharedLink> externalLinkMapper = new RowMapper<SharedLink>(){
		public SharedLink mapRow(ResultSet rs, int rowNum) throws SQLException {
			SharedLink link = new SharedLink(
				rs.getString("LINK_ID"),					
				rs.getInt("PUBLIC_SHARED") == 1 ,
				rs.getInt("OBJECT_TYPE"),
				rs.getLong("OBJECT_ID")
			);			
			return link;
		}		
	};
	
	public JdbcSharedLinkDao() {
	}
 
	public SharedLink getSharedLinkByObjectTypeAndObjectId(Integer objectType, Long objectId) {
		SharedLink link =  getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_SHARED_LINK_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				externalLinkMapper, 
				new SqlParameterValue (Types.NUMERIC, objectType ),
				new SqlParameterValue (Types.NUMERIC, objectId ));			
		return link;
	}
 
	public SharedLink getSharedLink(String linkId) {
		SharedLink link =  getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_SHARED_LINK_BY_LINK_ID").getSql(), 
				externalLinkMapper, 
				new SqlParameterValue (Types.VARCHAR, linkId ));			
		return link;		
	}
 
	public void saveOrUpdateSharedLink(SharedLink link) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_SHARED_LINK").getSql(), 	
				new SqlParameterValue (Types.VARCHAR, link.getLinkId() ), 
				new SqlParameterValue (Types.NUMERIC, link.getObjectType() ), 
				new SqlParameterValue (Types.NUMERIC, link.getObjectId() ), 
				new SqlParameterValue (Types.INTEGER, link.isPublicShared() ? 1 : 0  ) );
	}
 
	public void removeSharedLinkById(String linkId) {
		getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_SHARED_LINK_BY_LINK_ID").getSql(), 	
				new SqlParameterValue (Types.VARCHAR, linkId));	
	}
 
	public void removeSharedLinkByObjectTypeAndObjectId(int objectType, long objectId) {
		getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_SHARED_LINK_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 	
				new SqlParameterValue (Types.NUMERIC, objectType ),
				new SqlParameterValue (Types.NUMERIC, objectId ));	
	}
	
}
