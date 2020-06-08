package architecture.community.announce.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.announce.Announce;
import architecture.community.announce.AnnounceNotFoundException;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcAnnounceDao extends ExtendedJdbcDaoSupport implements AnnounceDao {
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;
	
	private String announcePropertyTableName = "AC_UI_ANNOUNCE_PROPERTY";
	private String announcePropertyPrimaryColumnName = "ANNOUNCE_ID";
	
	private final RowMapper<Announce> announceMapper = new RowMapper<Announce>() {
		public Announce mapRow(ResultSet rs, int rowNum) throws SQLException {
		    long announceId = rs.getLong("ANNOUNCE_ID");
		    Announce announce = new Announce(announceId);
		    announce.setObjectType(rs.getInt("OBJECT_TYPE"));
		    announce.setObjectId(rs.getLong("OBJECT_ID"));
		    announce.setUser( new UserTemplate(rs.getLong("USER_ID")));
		    announce.setSubject(rs.getString("SUBJECT"));
		    announce.setBody(rs.getString("BODY"));
		    announce.setStartDate(rs.getTimestamp("START_DATE"));
		    announce.setEndDate(rs.getTimestamp("END_DATE"));
		    announce.setCreationDate(rs.getDate("CREATION_DATE"));
		    announce.setModifiedDate(rs.getDate("MODIFIED_DATE"));
		    return announce;
		}
	};
	    
	public JdbcAnnounceDao() {
	}
	

	public void setAnnouncePropertyTableName(String announcePropertyTableName) {
		this.announcePropertyTableName = announcePropertyTableName;
	}


	public void setAnnouncePropertyPrimaryColumnName(String announcePropertyPrimaryColumnName) {
		this.announcePropertyPrimaryColumnName = announcePropertyPrimaryColumnName;
	}


	public Map<String, String> getAnnounceProperties(long announceId) {
		return propertyDao.getProperties(announcePropertyTableName, announcePropertyPrimaryColumnName, announceId);
	}

	public void deleteAnnounceProperties(long announceId) {
		propertyDao.deleteProperties(announcePropertyTableName, announcePropertyPrimaryColumnName, announceId);
	}
	
	public void setAnnounceProperties(long announceId, Map<String, String> props) {
		propertyDao.updateProperties(announcePropertyTableName, announcePropertyPrimaryColumnName, announceId, props);
	}
		
	public long getNextAnnounceId(){		
		return sequencerFactory.getNextValue(Models.ANNOUNCE.getObjectType(), Models.ANNOUNCE.name());
	}

	
	public Announce load(long announceId) throws AnnounceNotFoundException {
		try {
		    Announce announce = getExtendedJdbcTemplate().queryForObject(
			    getBoundSql("COMMUNITY_WEB.SELECT_ANNOUNCE_BY_ID").getSql(), announceMapper,
			    new SqlParameterValue(Types.NUMERIC, announceId));
		    announce.setProperties(getAnnounceProperties(announceId));
		    return announce;
		} catch (DataAccessException e) {
		    throw new AnnounceNotFoundException(e);
		}
	    }

	    public void update(Announce annoucne) {

			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_ANNOUNCE").getSql(),
				new SqlParameterValue(Types.VARCHAR, annoucne.getSubject()),
				new SqlParameterValue(Types.VARCHAR, annoucne.getBody()),
				new SqlParameterValue(Types.TIMESTAMP, annoucne.getStartDate()),
				new SqlParameterValue(Types.TIMESTAMP, annoucne.getEndDate()),
				new SqlParameterValue(Types.DATE, annoucne.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, annoucne.getAnnounceId()));
	
			setAnnounceProperties(annoucne.getAnnounceId(), annoucne.getProperties());
	    }

	    public void insert(Announce announce) {

			long announceIdToUse = announce.getAnnounceId();
			if (announceIdToUse <= 0)
			    announceIdToUse = getNextAnnounceId();
			announce.setAnnounceId(announceIdToUse);
	
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_ANNOUNCE").getSql(),
				new SqlParameterValue(Types.NUMERIC, announce.getAnnounceId()),
				new SqlParameterValue(Types.NUMERIC, announce.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, announce.getObjectId()),
				new SqlParameterValue(Types.NUMERIC, announce.getUser().getUserId()),
				new SqlParameterValue(Types.VARCHAR, announce.getSubject()),
				new SqlParameterValue(Types.VARCHAR, announce.getBody()),
				new SqlParameterValue(Types.TIMESTAMP, announce.getStartDate()),
				new SqlParameterValue(Types.TIMESTAMP, announce.getEndDate()),
				new SqlParameterValue(Types.DATE, announce.getCreationDate()),
				new SqlParameterValue(Types.DATE, announce.getModifiedDate()));
			setAnnounceProperties(announce.getAnnounceId(), announce.getProperties());

	    }

	    public void delete(Announce annoucne) {
			getJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ANNOUNCE").getSql(),
				new SqlParameterValue(Types.NUMERIC, annoucne.getAnnounceId()));
			deleteAnnounceProperties(annoucne.getAnnounceId());
	    }

	    public void move(Long fromId, Long toId) {

	    }

	    public List<Long> getAnnounceIdsForUser(long userId) {
	    		return getExtendedJdbcTemplate().query(
			getBoundSql("COMMUNITY_WEB.SELECT_ANNOUNCE_IDS_BY_USER_ID").getSql(), new RowMapper<Long>() {
			    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong(1);
			    }
			}, new SqlParameterValue(Types.NUMERIC, userId));

	    }

	    public List<Long> getAnnounceIds(int objectType, long objectId) {
		return getExtendedJdbcTemplate().query(
			getBoundSql("COMMUNITY_WEB.SELECT_ANNOUNCE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
			new RowMapper<Long>() {
			    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong(1);
			    }
			}, new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));
	    }

	    public List<Long> getAnnounceIds() {
		return getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_WEB.SELECT_ALL_ANNOUNCE_IDS").getSql(),
			new RowMapper<Long>() {
			    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong(1);
			    }
			});
	    }

	    public int getAnnounceCount(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForObject(
			getBoundSql("COMMUNITY_WEB.COUNT_ANNOUNCE_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
			Integer.class,
			new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId)
			);
	    }

	    public int getAnnounceCount(int objectType, long objectId, Date endDate) {
		return getExtendedJdbcTemplate().queryForObject(
			getBoundSql("COMMUNITY_WEB.COUNT_ANNOUNCE_BY_OBJECT_TYPE_AND_OBJECT_ID_AND_END_DATE").getSql(),
			Integer.class,
			new SqlParameterValue(Types.NUMERIC, objectType), 
			new SqlParameterValue(Types.NUMERIC, objectId),
			new SqlParameterValue(Types.TIMESTAMP, endDate == null ? Calendar.getInstance().getTime() : endDate));
	    }

	    public int getAnnounceCount(int objectType, long objectId, Date startDate, Date endDate) {
		return getExtendedJdbcTemplate().queryForObject(
			getBoundSql("COMMUNITY_WEB.COUNT_ANNOUNCE_BY_OBJECT_TYPE_AND_OBJECT_ID_AND_START_DATE_AND_END_DATE").getSql(),
			Integer.class,
			new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId),
			new SqlParameterValue(Types.TIMESTAMP, startDate == null ? new Date(0x8000000000000000L) : startDate),
			new SqlParameterValue(Types.TIMESTAMP, endDate == null ? Calendar.getInstance().getTime() : endDate));
	    }

}
