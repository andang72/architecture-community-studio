package architecture.community.category.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.category.Category;
import architecture.community.category.CategoryNotFoundException;
import architecture.community.model.Models;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcCategoryDao extends ExtendedJdbcDaoSupport implements CategoryDao {

	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;

	private String categoryPropertyTableName = "AC_CATEGORY_PROPERTY";
	private String categoryPropertyPrimaryColumnName = "CATEGORY_ID";

	private final RowMapper<Category> categoryMapper = new RowMapper<Category>() {
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			long announceId = rs.getLong("CATEGORY_ID");
			Category category = new Category(announceId);
			category.setObjectType(rs.getInt("OBJECT_TYPE"));
			category.setObjectId(rs.getLong("OBJECT_ID"));
			category.setName(rs.getString("NAME"));
			category.setDisplayName(rs.getString("DISPLAY_NAME"));
			category.setDescription(rs.getString("DESCRIPTION"));
			category.setCreationDate(rs.getDate("CREATION_DATE"));
			category.setModifiedDate(rs.getDate("MODIFIED_DATE"));
			return category;
		}
	};

	public Map<String, String> getCategoryProperties(long categoryId) {
		return propertyDao.getProperties(categoryPropertyTableName, categoryPropertyPrimaryColumnName, categoryId);
	}

	public void deleteCategoryProperties(long categoryId) {
		propertyDao.deleteProperties(categoryPropertyTableName, categoryPropertyPrimaryColumnName, categoryId);
	}

	public void setCategoryProperties(long categoryId, Map<String, String> props) {
		propertyDao.updateProperties(categoryPropertyTableName, categoryPropertyPrimaryColumnName, categoryId, props);
	}

	public long getNextCategoryId() {
		return sequencerFactory.getNextValue(Models.CATEGORY.getObjectType(), Models.CATEGORY.name());
	}

	public Category load(long categoryId) throws CategoryNotFoundException {
		try {
			Category category = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_CS.SELECT_CATEGORY_BY_ID").getSql(), categoryMapper,
					new SqlParameterValue(Types.NUMERIC, categoryId));
			category.setProperties(getCategoryProperties(categoryId));
			return category;
		} catch (DataAccessException e) {
			throw new CategoryNotFoundException(e);
		}
	}

	public void saveOrUpdate(Category category) {
		if (category.getCategoryId() < 1) {
			// insert case
			if (category.getName() == null)
				throw new IllegalArgumentException();
			if ("".equals(category.getDescription()))
				category.setDescription(null);
			category.setCategoryId(getNextCategoryId());

			try {
				Date now = new Date();
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_CS.INSERT_CATEGORY").getSql(),
						new SqlParameterValue(Types.NUMERIC, category.getCategoryId()),
						new SqlParameterValue(Types.NUMERIC, category.getObjectType()),
						new SqlParameterValue(Types.NUMERIC, category.getObjectId()),
						new SqlParameterValue(Types.VARCHAR, category.getName()),
						new SqlParameterValue(Types.VARCHAR, category.getDisplayName()),
						new SqlParameterValue(Types.VARCHAR, category.getDescription()),
						new SqlParameterValue(Types.TIMESTAMP,
								category.getCreationDate() != null ? category.getCreationDate() : now),
						new SqlParameterValue(Types.TIMESTAMP,
								category.getModifiedDate() != null ? category.getModifiedDate() : now));

				if (category.getProperties().size() > 0)
					setCategoryProperties(category.getCategoryId(), category.getProperties());

			} catch (DataAccessException e) {
				throw e;
			}

		} else {
			// update case
			Date now = new Date();
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_CS.UPDATE_CATEGORY").getSql(),
					new SqlParameterValue(Types.NUMERIC, category.getObjectType()),
					new SqlParameterValue(Types.NUMERIC, category.getObjectId()),
					new SqlParameterValue(Types.VARCHAR, category.getName()),
					new SqlParameterValue(Types.VARCHAR, category.getDisplayName()),
					new SqlParameterValue(Types.VARCHAR, category.getDescription()),
					new SqlParameterValue(Types.TIMESTAMP,
							category.getModifiedDate() != null ? category.getModifiedDate() : now),
					new SqlParameterValue(Types.NUMERIC, category.getCategoryId()));
			deleteCategoryProperties(category.getCategoryId());
			if (category.getProperties().size() > 0)
				setCategoryProperties(category.getCategoryId(), category.getProperties());

		}

	}

	public void update(Category category) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_CS.UPDATE_CATEGORY").getSql(),
				new SqlParameterValue(Types.NUMERIC, category.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, category.getObjectId()),
				new SqlParameterValue(Types.NUMERIC, category.getName()),
				new SqlParameterValue(Types.VARCHAR, category.getDisplayName()),
				new SqlParameterValue(Types.VARCHAR, category.getDescription()),
				new SqlParameterValue(Types.DATE, category.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, category.getCategoryId()));

		setCategoryProperties(category.getCategoryId(), category.getProperties());
	}

	public void insert(Category category) {

		long categoryIdToUse = category.getCategoryId();
		if (categoryIdToUse <= 0)
			categoryIdToUse = getNextCategoryId();
		category.setCategoryId(categoryIdToUse);

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_CS.INSERT_CATEGORY").getSql(),
				new SqlParameterValue(Types.NUMERIC, category.getCategoryId()),
				new SqlParameterValue(Types.NUMERIC, category.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, category.getObjectId()),
				new SqlParameterValue(Types.NUMERIC, category.getName()),
				new SqlParameterValue(Types.VARCHAR, category.getDisplayName()),
				new SqlParameterValue(Types.VARCHAR, category.getDescription()),
				new SqlParameterValue(Types.DATE, category.getCreationDate()),
				new SqlParameterValue(Types.DATE, category.getModifiedDate()));
		setCategoryProperties(category.getCategoryId(), category.getProperties());

	}

	public void delete(Category category) {
		getJdbcTemplate().update(getBoundSql("COMMUNITY_CS.DELETE_CATEGORY").getSql(),
				new SqlParameterValue(Types.NUMERIC, category.getCategoryId()));
		deleteCategoryProperties(category.getCategoryId());
	}

}
