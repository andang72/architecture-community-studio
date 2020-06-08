package architecture.community.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.model.Models;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.util.StringUtils;

public class JdbcUserDao extends ExtendedJdbcDaoSupport implements UserDao {

	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;

	private String propertyTableName = "AC_UI_USER_PROPERTY";
	private String propertyPrimaryColumnName = "USER_ID";

	private final RowMapper<UserTemplate> userRowMapper = new RowMapper<UserTemplate>() {
		public UserTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserTemplate ut = new UserTemplate();
			ut.setUserId(rs.getLong("USER_ID"));
			ut.setUsername(rs.getString("USERNAME"));
			ut.setPassword(rs.getString("PASSWORD_HASH"));
			ut.setName(rs.getString("NAME"));
			ut.setNameVisible(rs.getInt("NAME_VISIBLE") == 1);
			ut.setFirstName(rs.getString("FIRST_NAME"));
			ut.setLastName(rs.getString("LAST_NAME"));
			ut.setEmail(rs.getString("EMAIL"));
			ut.setEmailVisible(rs.getInt("EMAIL_VISIBLE") == 1);
			ut.setEnabled(rs.getInt("USER_ENABLED") == 1);
			ut.setExternal(rs.getInt("USER_EXTERNAL") == 1);
			ut.setStatus(UserTemplate.Status.getById(rs.getInt("STATUS")));
			ut.setCreationDate(rs.getDate("CREATION_DATE"));
			ut.setModifiedDate(rs.getDate("MODIFIED_DATE"));
			return ut;
		}
	};

	private final RowMapper<User> securedUserRowMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserTemplate ut = new UserTemplate();
			ut.setUserId(rs.getLong("USER_ID"));
			ut.setUsername(rs.getString("USERNAME"));
			ut.setName(rs.getString("NAME"));
			ut.setNameVisible(rs.getInt("NAME_VISIBLE") == 1);
			ut.setFirstName(rs.getString("FIRST_NAME"));
			ut.setLastName(rs.getString("LAST_NAME"));
			ut.setEmail(rs.getString("EMAIL"));
			ut.setEmailVisible(rs.getInt("EMAIL_VISIBLE") == 1);
			ut.setEnabled(rs.getInt("USER_ENABLED") == 1);
			ut.setExternal(rs.getInt("USER_EXTERNAL") == 1);
			ut.setStatus(UserTemplate.Status.getById(rs.getInt("STATUS")));
			ut.setCreationDate(rs.getDate("CREATION_DATE"));
			ut.setModifiedDate(rs.getDate("MODIFIED_DATE"));
			return ut;
		}
	};

	public JdbcUserDao() {
		super();
	}

	public long getNextUserId() {
		return sequencerFactory.getNextValue(Models.USER.getObjectType(), Models.USER.name());
	}

	/**
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @return 프로퍼티에 해당하는 사용자 아이디 값들을 리턴한다.
	 */
	public List<Integer> getUserIdsWithUserProperty(String propertyName, String propertyValue) {
		return getExtendedJdbcTemplate().queryForList(getBoundSql("COMMUNITY_USER.SELECT_USER_ID_BY_PROPERTY").getSql(),
				Integer.class, new SqlParameterValue(Types.VARCHAR, propertyName),
				new SqlParameterValue(Types.VARCHAR, propertyValue));
	}

	public User getUserById(long userId) {
		if (userId <= 0L) {
			return null;
		}
		UserTemplate user = null;
		try {
			user = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.SELECT_USER_BY_ID").getSql(), userRowMapper, new SqlParameterValue(Types.NUMERIC, userId));
			user.setProperties(getUserProperties(user.getUserId()));
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() > 1) {
				logger.warn(CommunityLogLocalizer.format("010008", userId));
				throw e;
			}
		} catch (DataAccessException e) {
			logger.error(CommunityLogLocalizer.format("010007", userId), e);
		}
		return user;
	}

	public User getUserByEmail(String email) {
		if (StringUtils.isNullOrEmpty(email))
			return null;
		String emailMatch = email.replace('*', '%');
		UserTemplate user = null;
		try {
			user = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.SELECT_USER_BY_EMAIL").getSql(), userRowMapper, new SqlParameterValue(Types.VARCHAR, emailMatch));
			user.setProperties(getUserProperties(user.getUserId()));
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() > 1) {
				logger.warn(CommunityLogLocalizer.format("010010", email));
				throw e;
			}
		} catch (DataAccessException e) {
			logger.error(CommunityLogLocalizer.format("010011", emailMatch), e);
			throw e;
		}
		return user;
	}

	private String formatUsername(String username) {
		if (StringUtils.isNullOrEmpty(username))
			return null;
		boolean allowWhiteSpace = false;
		try {
			allowWhiteSpace = configService.getApplicationBooleanProperty("username.allowWhiteSpace", false);
		} catch (Throwable ignore) {
		}
		if (allowWhiteSpace) {
			String formattedUsername = "";
			Pattern p = Pattern.compile("\\s+");
			Matcher m = p.matcher(username.trim());
			if (m.find()) {
				formattedUsername = m.replaceAll(" ");
				username = formattedUsername;
			}
		} else {
			username = StringUtils.trimAllWhitespace(username);
		}
		return username;
	}

	public User createUser(User user) {
		UserTemplate template = new UserTemplate(user);
		if (template.getEmail() == null)
			throw new IllegalArgumentException(CommunityLogLocalizer.getMessage("010012"));
		// long nextUserId = getNextUserId() ;
		template.setUserId(getNextUserId());

		if ("".equals(template.getName()))
			template.setName(null);

		template.setEmail(template.getEmail().toLowerCase());
		if (template.getStatus() == null || template.getStatus() == User.Status.NONE)
			template.setStatus(User.Status.REGISTERED);

		boolean useLastNameFirstName = !StringUtils.isNullOrEmpty(template.getFirstName()) && !StringUtils.isNullOrEmpty(template.getLastName());

		try {
			Date now = new Date();
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.CREATE_USER").getSql(),
					new SqlParameterValue(Types.NUMERIC, template.getUserId()),
					new SqlParameterValue(Types.VARCHAR, template.getUsername()),
					new SqlParameterValue(Types.VARCHAR, template.getPasswordHash()),
					new SqlParameterValue(Types.VARCHAR, useLastNameFirstName ? template : user.getName()),
					new SqlParameterValue(Types.NUMERIC, template.isNameVisible() ? 1 : 0),
					new SqlParameterValue(Types.VARCHAR, useLastNameFirstName ? template.getFirstName() : null),
					new SqlParameterValue(Types.VARCHAR, useLastNameFirstName ? template.getLastName() : null),
					new SqlParameterValue(Types.VARCHAR, template.getEmail()),
					new SqlParameterValue(Types.NUMERIC, template.isEmailVisible() ? 1 : 0),
					new SqlParameterValue(Types.NUMERIC, template.isEnabled() ? 1 : 0),
					new SqlParameterValue(Types.NUMERIC, template.isExternal() ? 1 : 0),
					new SqlParameterValue(Types.NUMERIC, template.getStatus().getId()),
					new SqlParameterValue(Types.TIMESTAMP,
							template.getCreationDate() != null ? template.getCreationDate() : now),
					new SqlParameterValue(Types.TIMESTAMP,
							template.getModifiedDate() != null ? template.getModifiedDate() : now));
			setUserProperties(user.getUserId(), user.getProperties());
		} catch (DataAccessException e) {
			logger.error(CommunityLogLocalizer.getMessage("010013"), e);
			throw e;
		}
		return template;
	}

	public User getUserByUsername(String username) {
		if (StringUtils.isNullOrEmpty(username))
			return null;
		UserTemplate user = null;
		try {
			user = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_USER.SELECT_USER_BY_USERNAME").getSql(), userRowMapper, new SqlParameterValue(Types.VARCHAR, username));
			user.setProperties(getUserProperties(user.getUserId()));
		} catch (EmptyResultDataAccessException e) {
			logger.warn(CommunityLogLocalizer.format("010009", username), e);
		} catch (DataAccessException e) {
			logger.warn(CommunityLogLocalizer.getMessage("010004"), e);
		}
		return user;
	}

	public void deleteUser(User user) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.DELETE_USER_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, user.getUserId()));
		propertyDao.deleteProperties(propertyTableName, propertyPrimaryColumnName, user.getUserId());
	}

	public int getFoundUserCount(String nameOrEmail) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_USER.COUNT_USERS_BY_EMAIL_OR_NAME").getSql(), Integer.class,
				new SqlParameterValue(Types.VARCHAR, nameOrEmail), new SqlParameterValue(Types.VARCHAR, nameOrEmail));
	}

	public List<User> findUsers(String nameOrEmail) {
		List<User> users = getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_USER.SELECT_USER_BY_EMAIL_OR_NAME").getSql(), securedUserRowMapper,
				new SqlParameterValue(Types.VARCHAR, nameOrEmail), new SqlParameterValue(Types.VARCHAR, nameOrEmail));
		return users;
	}

	public List<Long> findUserIds(String nameOrEmail) {
		List<Long> users = getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_USER.SELECT_USER_IDS_BY_EMAIL_OR_NAME").getSql(), Long.class,
				new SqlParameterValue(Types.VARCHAR, nameOrEmail), new SqlParameterValue(Types.VARCHAR, nameOrEmail));
		return users;
	}

	public List<Long> findUserIds(String nameOrEmail, int startIndex, int numResults) {
		List<Long> users = getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_USER.SELECT_USER_IDS_BY_EMAIL_OR_NAME").getSql(), startIndex, numResults,
				Long.class, new SqlParameterValue(Types.VARCHAR, nameOrEmail),
				new SqlParameterValue(Types.VARCHAR, nameOrEmail));
		return users;
	}

	public int getUserCount() {
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.ALL_COUNT_USERS").getSql(),
				Integer.class);
	}

	public List<Long> getUserIds() {
		List<Long> users = getExtendedJdbcTemplate()
				.queryForList(getBoundSql("COMMUNITY_USER.SELECT_ALL_USER_IDS").getSql(), Long.class);
		return users;
	}

	public List<Long> getUserIds(int startIndex, int numResults) {
		List<Long> users = getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_USER.SELECT_ALL_USER_IDS").getSql(),
				startIndex, numResults, Long.class);
		return users;
	}

	public void updateUserProperty(User user) {
		setUserProperties(user.getUserId(), user.getProperties()); 
	}

	public User updateUser(User user) {
		UserTemplate userToUse = (UserTemplate) user;
		boolean useLastNameFirstName = userToUse.getFirstName() != null && userToUse.getLastName() != null;
		try {
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.UPDATE_USER").getSql(),
					new SqlParameterValue(Types.VARCHAR, userToUse.getUsername()),
					new SqlParameterValue(Types.VARCHAR, userToUse.getPasswordHash()),
					new SqlParameterValue(Types.VARCHAR, userToUse.getName()),
					new SqlParameterValue(Types.NUMERIC, userToUse.isNameVisible() ? 1 : 0),
					new SqlParameterValue(Types.VARCHAR, useLastNameFirstName ? userToUse.getFirstName() : null),
					new SqlParameterValue(Types.VARCHAR, useLastNameFirstName ? userToUse.getLastName() : null),
					new SqlParameterValue(Types.VARCHAR, userToUse.getEmail()),
					new SqlParameterValue(Types.NUMERIC, userToUse.isEmailVisible() ? 1 : 0),
					new SqlParameterValue(Types.NUMERIC, userToUse.isEnabled() ? 1 : 0),
					new SqlParameterValue(Types.NUMERIC, userToUse.getStatus().getId()),
					new SqlParameterValue(Types.TIMESTAMP,
							userToUse.getModifiedDate() != null ? userToUse.getModifiedDate() : new Date()),
					new SqlParameterValue(Types.NUMERIC, userToUse.getUserId()));
			setUserProperties(user.getUserId(), user.getProperties()); 
		} catch (DataAccessException e) {
			String message = "Failed to update user.";
			logger.error(message, e);
			throw e;
		}
		return user;
	}

	public Map<String, String> getUserProperties(long userId) {
		return propertyDao.getProperties(propertyTableName, propertyPrimaryColumnName, userId);
	}

	public void setUserProperties(long userId, Map<String, String> props) {
		propertyDao.updateProperties(propertyTableName, propertyPrimaryColumnName, userId, props);
	}
}
