package architecture.community.tag.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.model.Models;
import architecture.community.tag.ContentTag;
import architecture.community.tag.DefaultContentTag;
import architecture.community.tag.dao.TagDao;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcTagDao extends ExtendedJdbcDaoSupport implements TagDao {

	protected static final RowMapper<ContentTag> conentTagRowMapper = new RowMapper<ContentTag>() {
		public ContentTag mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new DefaultContentTag(rs.getLong(1), rs.getString(2), rs.getTimestamp(3));
		}
	};

	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;
	
	private String tagPropertyTableName = "AC_UI_TAG_PROPERTY";
	
	private String tagPropertyPrimaryColumnName = "TAG_ID";

	public JdbcTagDao() {
	}


	/**
	 * @param tagPropertyTableName
	 *            설정할 tagPropertyTableName
	 */
	public void setTagPropertyTableName(String tagPropertyTableName) {
		this.tagPropertyTableName = tagPropertyTableName;
	}

	/**
	 * @param tagPropertyPrimaryColumnName
	 *            설정할 tagPropertyPrimaryColumnName
	 */
	public void setTagPropertyPrimaryColumnName(String tagPropertyPrimaryColumnName) {
		this.tagPropertyPrimaryColumnName = tagPropertyPrimaryColumnName;
	}

	
	
	public void addTag(long tagId, int objectType, long objectId) {
		Date now = Calendar.getInstance().getTime();
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_TAG").getSql(),
				new SqlParameterValue(Types.NUMERIC, tagId), new SqlParameterValue(Types.NUMERIC, objectType),
				new SqlParameterValue(Types.NUMERIC, objectId), new SqlParameterValue(Types.TIMESTAMP, now));

	}

	public void removeTag(long tagId, int objectType, long objectId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_TAG").getSql(),
				new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId),
				new SqlParameterValue(Types.NUMERIC, tagId));
	}

	public int countTags(long tagId) {
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.COUNT_TAG").getSql(),
				Integer.class, new SqlParameterValue(Types.NUMERIC, tagId));
	}

	public List<Long> getTagIds(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_WEB.SELECT_TAG_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));
	}

	public ContentTag getContentTagById(long tagId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_CONTENT_TAG_BY_ID").getSql(), conentTagRowMapper,
				new SqlParameterValue(Types.NUMERIC, tagId));
	}

	public ContentTag getContentTagByName(String name) {
		try {
			return getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_WEB.SELECT_CONTENT_TAG_BY_NAME").getSql(), conentTagRowMapper,
					new SqlParameterValue(Types.VARCHAR, name));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void createContentTag(ContentTag tag) {
		if (tag.getName() == null || tag.getName().length() == 0) {
			throw new IllegalStateException("Tag must have a non null name.");
		} else {
			long tagId = tag.getTagId();
			if (tagId == -1L) {
				tagId = getNextTagId();
				if (tag instanceof DefaultContentTag) {
					((DefaultContentTag) tag).setTagId(tagId);
				}
			}
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_CONTENT_TAG").getSql(),
					new SqlParameterValue(Types.NUMERIC, tagId), new SqlParameterValue(Types.VARCHAR, tag.getName()),
					new SqlParameterValue(Types.TIMESTAMP, tag.getCreationDate()));
			return;
		}
	}

	public void deleteContentTag(long tagId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_CONTENT_TAG").getSql(), new SqlParameterValue(Types.NUMERIC, tagId));
	}
	
	public long getNextTagId(){		
		return sequencerFactory.getNextValue(Models.TAG.getObjectType(), Models.TAG.name());
	}
}
