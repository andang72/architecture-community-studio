package architecture.community.page.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;

import architecture.community.model.Models;
import architecture.community.page.BodyContent;
import architecture.community.page.BodyType;
import architecture.community.page.DefaultBodyContent;
import architecture.community.page.DefaultPage;
import architecture.community.page.Page;
import architecture.community.page.PageState;
import architecture.community.page.PageVersion;
import architecture.community.page.PageVersionHelper;
import architecture.community.page.dao.PageDao;
import architecture.community.user.UserTemplate;
import architecture.community.util.SecurityHelper;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport; 

public class JdbcPageDao extends ExtendedJdbcDaoSupport implements PageDao {

	private int DEFAULT_PAGE_VERSION = 1;

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
 	
	private final RowMapper<BodyContent> bodyContentMapper = new RowMapper<BodyContent>() {
		public BodyContent mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultBodyContent body = new DefaultBodyContent();
			body.setBodyId(rs.getLong("BODY_ID"));
			body.setPageId(rs.getLong("PAGE_ID"));
			body.setBodyType(BodyType.getBodyTypeById(rs.getInt("BODY_TYPE")));
			body.setBodyText(rs.getString("BODY_TEXT"));
			return body;
		}
	};

	public JdbcPageDao() {
	}

	public long getNextPageId(){
		logger.debug("next id for {}, {}", Models.PAGE.getObjectType(), Models.PAGE.name() );
		return sequencerFactory.getNextValue(Models.PAGE.getObjectType(), Models.PAGE.name());
	}	
	

	/**
	 * 새로운 페이지 생성
	 */
	public void create(Page page) {

		long nextPageId = getNextPageId();
		page.setPageId(nextPageId);
		page.setVersionId(DEFAULT_PAGE_VERSION);

		// binary body data handleing

		String tempPrefix = configService.getApplicationProperty("components.page.temporaryPagePrefix", "tempPAGE-");
		if (page.getName() == null || page.getName().startsWith(tempPrefix)) {
			page.setName((new StringBuilder()).append(configService.getApplicationProperty("components.page.pagePrefix", "PAGE-")).append(nextPageId).toString());
		}

		// INSERT V2_PAGE
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.CREATE_PAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()),
				new SqlParameterValue(Types.NUMERIC, page.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, page.getObjectId()),
				new SqlParameterValue(Types.VARCHAR, page.getName()),
				page.getPattern() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getPattern()),	
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()),
				new SqlParameterValue(Types.NUMERIC, page.getUser().getUserId()),
				new SqlParameterValue(Types.TIMESTAMP, page.getCreationDate()),
				new SqlParameterValue(Types.TIMESTAMP, page.getModifiedDate()));

		insertPageVersion(page);
		insertPageBody(page);
		insertProperties(page);
	}
	
	private void insertProperties(Page page) {
		if (page.getProperties() != null && !page.getProperties().isEmpty()) {
			Map<String, String> properties = page.getProperties();
			final List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>( properties.entrySet());
			final long pageId = page.getPageId();
			final long pageVersionId = page.getVersionId();
			getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_PROPERTY").getSql(),
					new BatchPreparedStatementSetter() {
						public int getBatchSize() {
							return entryList.size();
						}

						public void setValues(PreparedStatement ps, int index) throws SQLException {
							Map.Entry<String, String> e = entryList.get(index);
							ps.setLong(1, pageId);
							ps.setLong(2, pageVersionId);
							ps.setString(3, e.getKey());
							ps.setString(4, e.getValue());
						}
					});
		}
	}

	private void updateProperties(final Page page) {
		Map<String, String> oldProps = loadProperties(page);
		logger.debug("old:" + oldProps);
		logger.debug("new:" + page.getProperties());

		final List<String> deleteKeys = getDeletedPropertyKeys(oldProps, page.getProperties());
		final List<String> modifiedKeys = getModifiedPropertyKeys(oldProps, page.getProperties());
		final List<String> addedKeys = getAddedPropertyKeys(oldProps, page.getProperties());
		logger.debug("deleteKeys:" + deleteKeys.size());
		if (!deleteKeys.isEmpty()) {
			getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_PROPERTY_BY_NAME").getSql(),
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setLong(1, page.getPageId());
							ps.setLong(2, page.getVersionId());
							ps.setString(3, deleteKeys.get(i));
						}

						public int getBatchSize() {
							return deleteKeys.size();
						}
					});
		}
		logger.debug("modifiedKeys:" + modifiedKeys.size());
		if (!modifiedKeys.isEmpty()) {
			getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_PROPERTY_BY_NAME").getSql(),
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							String key = modifiedKeys.get(i);
							String value = page.getProperties().get(key);
							logger.debug("batch[" + key + "=" + value + "]");
							ps.setString(1, value);
							ps.setLong(2, page.getPageId());
							ps.setLong(3, page.getVersionId());
							ps.setString(4, key);
						}
						public int getBatchSize() {
							return modifiedKeys.size();
						}
					});
		}
		logger.debug("addedKeys:" + addedKeys.size());
		if (!addedKeys.isEmpty()) {
			getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_PROPERTY").getSql(),
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setLong(1, page.getPageId());
							ps.setLong(2, page.getVersionId());
							String key = addedKeys.get(i);
							String value = page.getProperties().get(key);
							logger.debug("batch[" + key + "=" + value + "]");
							ps.setString(3, key);
							ps.setString(4, value);
						}
						public int getBatchSize() {
							return addedKeys.size();
						}
					});
		}
	}

	private List<String> getDeletedPropertyKeys(Map<String, String> oldProps, Map<String, String> newProps) {
		HashMap<String, String> temp = new HashMap<String, String>(oldProps);
		Set<String> oldKeys = temp.keySet();
		Set<String> newKeys = newProps.keySet();
		for (String key : newKeys)
			oldKeys.remove(key);
		return Arrays.asList(oldKeys.toArray(new String[oldKeys.size()]));

	}

	private List<String> getModifiedPropertyKeys(Map<String, String> oldProps, Map<String, String> newProps) {
		HashMap<String, String> temp = new HashMap<String, String>(oldProps);
		Set<String> oldKeys = temp.keySet();
		Set<String> newKeys = newProps.keySet();
		oldKeys.retainAll(newKeys);
		List<String> modified = new ArrayList<String>();
		for (String key : oldKeys) {
			logger.debug(key + " equals:[" + newProps.get(key) + "]=[" + oldProps.get(key) + "]" + StringUtils.equals(newProps.get(key), oldProps.get(key)));
			if (!StringUtils.equals(newProps.get(key), oldProps.get(key)))
				modified.add(key);
		}
		return modified;
	}

	/**
	 * return key values
	 * 
	 * @param oldProps
	 * @param newProps
	 * @return
	 */
	private List<String> getAddedPropertyKeys(Map<String, String> oldProps, Map<String, String> newProps) {
		HashMap<String, String> temp = new HashMap<String, String>(oldProps);
		Set<String> oldKeys = temp.keySet();
		Set<String> newKeys = newProps.keySet();
		List<String> added = new ArrayList<String>();
		for (String key : newKeys) {
			if (!oldKeys.contains(key)) {
				added.add(key);
			}
		}
		return added;
	}

	private Map<String, String> loadProperties(Page page) {
		return getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_PROPERTIES").getSql(),
				new Object[] { page.getPageId(), page.getVersionId() }, new ResultSetExtractor<Map<String, String>>() {
					public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
						Map<String, String> rows = new HashMap<String, String>();
						while (rs.next()) {
							String key = rs.getString(1);
							String value = rs.getString(2);
							rows.put(key, value);
						}
						return rows;
					}
				});
	}

	private void insertPageVersion(Page page) {
		Date now = Calendar.getInstance().getTime();
		if (page.getVersionId() > 1) {
			page.setModifiedDate(now);
		}
		if (page.getPageState() == PageState.PUBLISHED) {
			// clean up on publish
			cleanupVersionsOnPublish(page);
		}

		// INSERT V2_PAGE_VERSION
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_VERSION").getSql(),
			new SqlParameterValue(Types.NUMERIC, page.getPageId()),
			new SqlParameterValue(Types.NUMERIC, page.getVersionId()),
			new SqlParameterValue(Types.VARCHAR, page.getPageState().name().toLowerCase()),
			new SqlParameterValue(Types.VARCHAR, page.getTitle()), 
			page.getSummary() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getSummary()),
			page.getTemplate() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getTemplate()), 
			page.getScript() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getScript()), 
			new SqlParameterValue(Types.NUMERIC, page.isSecured() ? 1 : 0 ),								
			new SqlParameterValue(Types.NUMERIC, page.getVersionId() <= 1 ? page.getUser().getUserId() : SecurityHelper.getUser().getUserId()),
			new SqlParameterValue(Types.DATE, page.getCreationDate()),
			new SqlParameterValue(Types.DATE, page.getModifiedDate()));
	}

	private void cleanupVersionsOnPublish(Page page) {
		if (page.getVersionId() > 0) {
			try {
				int pubishedVersion = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_PAGE.SELECT_PUBLISHED_PAGE_VERSION_NUMBER").getSql(), Integer.class,
					new SqlParameterValue(Types.NUMERIC, page.getPageId()));
				page.setVersionId(pubishedVersion + 1);
			} catch (EmptyResultDataAccessException e) {
				Integer maxArchiveId = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_PAGE.SELECT_MAX_ARCHIVED_PAGE_VERSION_NUMBER").getSql(), Integer.class,
					new SqlParameterValue(Types.NUMERIC, page.getPageId()));
				if ( maxArchiveId!= null && maxArchiveId > 0)
					page.setVersionId(maxArchiveId + 1);
				else
					page.setVersionId(1);
			}
			List<Long> toDelete = getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_PAGE.SELECT_DRAFT_PAGE_VERSIONS").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, page.getPageId()));
			for (Long version : toDelete)
				deleteVersion(page, version.intValue());
		}
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_STATE_TO_ARCHIVED").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()),
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()));

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_VISION_NUMBER").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()));

	}

	private void deleteVersion(Page page, int version) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_BODY_VERSION").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()), new SqlParameterValue(Types.NUMERIC, version));
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_VERSION").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()), new SqlParameterValue(Types.NUMERIC, version));
	}

	private void insertPageBody(final Page page) {
		long bodyId = -1L;
		if (page.getBodyText() != null) {
			boolean newBodyRequired = false;
			if (page.getVersionId() == 1) {
				newBodyRequired = true;
			} else {
				// load body from database ...
				List<BodyContent> results = getExtendedJdbcTemplate().query(
						getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_BODY").getSql(), bodyContentMapper,
						new SqlParameterValue(Types.NUMERIC, page.getPageId()),
						new SqlParameterValue(Types.NUMERIC, Integer.valueOf(page.getVersionId() - 1)));
				String preTextBody = null;
				for (BodyContent bodyContent : results) {
					bodyId = bodyContent.getBodyId();
					preTextBody = bodyContent.getBodyText();
				}
				String textBody = page.getBodyText();
				if (preTextBody == null || !preTextBody.equals(textBody))
					newBodyRequired = true;
			}

			if (newBodyRequired) {
				bodyId = getNextPageId();
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_BODY").getSql(),
						new SqlParameterValue(Types.NUMERIC, bodyId),
						new SqlParameterValue(Types.NUMERIC, page.getPageId()),
						new SqlParameterValue(Types.NUMERIC, page.getBodyContent().getBodyType().getId()),
						new SqlParameterValue(Types.CLOB, new SqlLobValue(page.getBodyText(), getLobHandler())));
				
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_BODY_VERSION").getSql(),
						new SqlParameterValue(Types.NUMERIC, bodyId),
						new SqlParameterValue(Types.NUMERIC, page.getPageId()),
						new SqlParameterValue(Types.NUMERIC, page.getVersionId()));
			}
		}
	}

	public void update(Page page, boolean isNewVersion) {
		int prevVersionId = page.getVersionId();
		Date now = Calendar.getInstance().getTime();
		if (isNewVersion) {
			int maxVersionId = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_PAGE.SELECT_MAX_PAGE_VERSION_NUMBER").getSql(), Integer.class,
					new SqlParameterValue(Types.NUMERIC, page.getPageId()));
			page.setVersionId(maxVersionId + 1);
		}

		page.setModifiedDate(now);
		// update page ...
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()),
				new SqlParameterValue(Types.NUMERIC, page.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, page.getObjectId()),
				new SqlParameterValue(Types.VARCHAR, page.getName()),
				page.getPattern() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getPattern()),	
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()),
				new SqlParameterValue(Types.NUMERIC, page.getUser().getUserId()),
				new SqlParameterValue(Types.TIMESTAMP, page.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()));

		updateProperties(page);

		if (isNewVersion) {
			insertPageVersion(page);
			insertPageBody(page);
		} else {
			updatePageVersion(page, prevVersionId);
			updatePageBody(page, prevVersionId);
		}
	}

	private void updatePageVersion(Page page, int prevVersionId) {
		Date now = Calendar.getInstance().getTime();

		if (page.getPageState() == PageState.PUBLISHED) {
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_STATE_TO_ARCHIVED").getSql(),
					new SqlParameterValue(Types.NUMERIC, page.getPageId()),
					new SqlParameterValue(Types.NUMERIC, page.getVersionId()));
		}
		if (page.getVersionId() > 0) {
			page.setModifiedDate(now);
			long modifierId = page.getUser().getUserId() <= 0L ? page.getUser().getUserId()
					: page.getUser().getUserId();
			// update page version
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_VERSION").getSql(),
				new SqlParameterValue(Types.VARCHAR, page.getPageState().name().toLowerCase()),
				new SqlParameterValue(Types.VARCHAR, page.getTitle()),
				new SqlParameterValue(Types.VARCHAR, page.getSummary()),
				page.getTemplate() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getTemplate()), 
				page.getScript() == null ? new SqlParameterValue(Types.NULL, null) : new SqlParameterValue(Types.VARCHAR, page.getScript()), 		
				new SqlParameterValue(Types.NUMERIC, page.isSecured() ? 1 : 0 ),			
				new SqlParameterValue(Types.NUMERIC, modifierId),
				new SqlParameterValue(Types.DATE, page.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()),
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()));

		}
	}

	private void updatePageBody(Page page, int prevVersionId) {
		long bodyId = -1L;

		try {
			bodyId = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_PAGE.SELETE_PAGE_BODY_ID").getSql(), Long.class,
					new SqlParameterValue(Types.NUMERIC, page.getPageId()),
					new SqlParameterValue(Types.NUMERIC, prevVersionId));
		} catch (EmptyResultDataAccessException e) {
		}
		if (page.getBodyText() != null) {
			if (bodyId != -1L) {
				final long bodyIdToUse = bodyId;
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_BODY").getSql(),
						new SqlParameterValue(Types.INTEGER, page.getBodyContent().getBodyType().getId()),
						new SqlParameterValue(Types.VARCHAR, page.getBodyContent().getBodyText()),
						new SqlParameterValue(Types.NUMERIC, bodyIdToUse));
			} else {
				final long bodyIdToUse = getNextPageId();
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_BODY").getSql(),
						new SqlParameterValue(Types.NUMERIC, bodyIdToUse),
						new SqlParameterValue(Types.NUMERIC, page.getPageId()),
						new SqlParameterValue(Types.INTEGER, page.getBodyContent().getBodyType().getId()),
						new SqlParameterValue(Types.VARCHAR, page.getBodyContent().getBodyText()));
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.INSERT_PAGE_BODY_VERSION").getSql(),
						new SqlParameterValue(Types.NUMERIC, bodyIdToUse),
						new SqlParameterValue(Types.NUMERIC, page.getPageId()),
						new SqlParameterValue(Types.NUMERIC, prevVersionId));
			}
		}
	}

	public void delete(Page page) {

		if (page.getVersionId() == -1) {
			List<Long> bodyIds = getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_PAGE.SELETE_PAGE_BODY_IDS").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, page.getPageId()));
			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_BODY_VERSIONS").getSql(),
				new SqlParameterValue(Types.NUMERIC, page.getPageId()));
			for (long bodyId : bodyIds) {
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_BODY").getSql(), new SqlParameterValue(Types.NUMERIC, bodyId));
			}
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_VERSIONS").getSql(), new SqlParameterValue(Types.NUMERIC, page.getPageId()));
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_PROPERTIES").getSql(), new SqlParameterValue(Types.NUMERIC, page.getPageId()));
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_PAGE").getSql(), new SqlParameterValue(Types.NUMERIC, page.getPageId()));
		}
		
	}

	public Page getPageById(long pageId) {
		return getPageById(pageId, -1);
	}

	public Page getPageById(long pageId, int versionNumber) {
		return load(pageId, versionNumber <= 0 ? getPageVersion(pageId) : versionNumber);
	}

	private Page load(long pageId, int versionNumber) {
		if (pageId <= 0)
			return null;

		final DefaultPage page = new DefaultPage();
		page.setPageId(pageId);
		page.setVersionId(versionNumber);
		getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_BY_ID_AND_VERSION").getSql(),
			new RowMapper<Page>() {			
				public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
					page.setName(rs.getString("NAME"));
					page.setObjectType(rs.getInt("OBJECT_TYPE"));
					page.setObjectId(rs.getLong("OBJECT_ID"));
					page.setPageState(PageState.valueOf(rs.getString("STATE").toUpperCase()));
						
					page.setUser(new UserTemplate(rs.getLong("USER_ID")));
					if (rs.wasNull())
						page.setUser(new UserTemplate(-1L));
						
					page.setTitle(rs.getString("TITLE"));
					page.setSummary(rs.getString("SUMMARY"));
					page.setTemplate(rs.getString("TEMPLATE"));
					page.setPattern(rs.getString("PATTERN"));
					if(StringUtils.isNotEmpty(rs.getString("SCRIPT"))) {
						page.setScript(rs.getString("SCRIPT"));
					}
					page.setSecured(rs.getInt("SECURED") == 1 ? true : false);
					page.setCreationDate(rs.getTimestamp("CREATION_DATE"));
					page.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
					return page;
				}
			}, 
			new SqlParameterValue(Types.NUMERIC, page.getPageId()),
			new SqlParameterValue(Types.NUMERIC, page.getVersionId()));

		if (page.getName() == null)
			return null;

		try {
			BodyContent bodyContent = getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_BODY").getSql(), 
				bodyContentMapper,
				new SqlParameterValue(Types.NUMERIC, page.getPageId()),
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()));
			page.setBodyContent(bodyContent);
		} catch (EmptyResultDataAccessException e) {
			page.setBodyContent(new DefaultBodyContent(-1L, page.getPageId(), BodyType.FREEMARKER, null ));
		}

		if (StringUtils.isEmpty( page.getBodyText() ) ) {
			getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_PAGE.DELETE_PAGE_BODY_VERSION").getSql(), 
				new SqlParameterValue(Types.NUMERIC, page.getPageId()),
				new SqlParameterValue(Types.NUMERIC, page.getVersionId()));
		}

		Map<String, String> properties = loadProperties(page);
		page.getProperties().putAll(properties);
		
		return page;
	}

	public int getPageVersion(long pageId) {

		PageVersion v = PageVersionHelper.getDeletedPageVersion(pageId);
		if (v == null)
			v = PageVersionHelper.getPublishedPageVersion(pageId);
		if (v == null)
			v = PageVersionHelper.getNewestPageVersion(pageId);
		if (v != null)
			return v.getVersionNumber();
		else
			return -1;
	}

	public Page getPageByName(String name) {
		long pageId = -1L;
		try {
			pageId = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_ID_BY_NAME").getSql(), Long.class,
					new SqlParameterValue(Types.VARCHAR, name));
		} catch (DataAccessException e) {
			return null;
		}

		return getPageById(pageId, -1);
	}

	public Page getPageByName(String name, int versionNumber) {
		long pageId = -1L;
		try {
			pageId = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_ID_BY_NAME").getSql(), Long.class, new SqlParameterValue(Types.VARCHAR, name));
		} catch (DataAccessException e) {
			return null;
		}
		return load(pageId, versionNumber);
	}

	public Page getPageByTitle(int objectType, long objectId, String title) {
		Long resutls[] = getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_BY_OBJECT_TYPE_AND_OBJECT_ID_AND_TITLE").getSql(),
				new RowMapper<Long[]>() {
					public Long[] mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new Long[] { rs.getLong("PAGE_ID"), rs.getLong("VERSION_ID") };
					}
				}, new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId),
				new SqlParameterValue(Types.VARCHAR, title));
		if (resutls == null || resutls.length == 0) {
			return null;
		}
		return load(resutls[0].longValue(), resutls[1].intValue());
	}

	public int getPageCount(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.COUNT_PAGE_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));
	}
	
	public List<Long> getPageIds(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId));
	}

	public List<Long> getPageIds(int objectType, long objectId, int startIndex, int maxResults) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				startIndex,
				maxResults, 
				Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType ),
				new SqlParameterValue(Types.NUMERIC, objectId )
				);
	}
 
	public List<Long> getPageIds(int objectType, PageState state) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_IDS_BY_OBJECT_TYPE_AND_STATE").getSql(), Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType),
				new SqlParameterValue(Types.VARCHAR, state.name().toLowerCase()));
	}
 
	public List<Long> getPageIds(int objectType, PageState state, int startIndex, int maxResults) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_IDS_BY_OBJECT_TYPE_AND_STATE").getSql(), 
				startIndex, 
				maxResults,
				Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType ),
				new SqlParameterValue(Types.VARCHAR, state.name().toLowerCase() )
				);
	}
 
	public int getPageCount(int objectType, PageState state) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.COUNT_PAGE_BY_OBJECT_TYPE_AND_STATE").getSql(), Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType),
				new SqlParameterValue(Types.VARCHAR, state.name().toLowerCase()));
	}
 
	public List<Long> getPageIds(int objectType, long objectId, PageState state) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID_AND_STATE").getSql(),
				Long.class, new SqlParameterValue(Types.NUMERIC, objectType),
				new SqlParameterValue(Types.NUMERIC, objectId),
				new SqlParameterValue(Types.VARCHAR, state.name().toLowerCase()));
	}
 
	public List<Long> getPageIds(int objectType, long objectId, PageState state, int startIndex, int maxResults) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID_AND_STATE").getSql(),
				startIndex, 
				maxResults, 
				Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType),
				new SqlParameterValue(Types.NUMERIC, objectId),
				new SqlParameterValue(Types.VARCHAR, state.name().toLowerCase())
				);
	}
 
	public int getPageCount(int objectType, long objectId, PageState state) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.COUNT_PAGE_BY_OBJECT_TYPE_AND_OBJECT_ID_AND_STATE").getSql(), Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType), new SqlParameterValue(Types.NUMERIC, objectId),
				new SqlParameterValue(Types.VARCHAR, state.name().toLowerCase()));
	}
 
	public List<Page> getAllPageHasPatterns() {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_PAGE.SELECT_ALL_PATTERN_AND_ID").getSql(),
				new RowMapper<Page>() {
					public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
						Page page = new DefaultPage(rs.getLong(1));
						page.setPattern(rs.getString(2));
						return page;
					}
				});
	}

}
