package architecture.community.security.spring.rememberme.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcTokenRepositoryDao extends ExtendedJdbcDaoSupport implements PersistentTokenRepository{

	
	public void createNewToken(PersistentRememberMeToken token) {			
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.CREATE_REMEMBER_USER_TOKEN").getSql(),
				new SqlParameterValue(Types.VARCHAR, token.getSeries()),
				new SqlParameterValue(Types.VARCHAR, token.getUsername()),
				new SqlParameterValue(Types.VARCHAR, token.getTokenValue()),
				new SqlParameterValue(Types.TIMESTAMP, token.getDate()));
	}

	public void updateToken(String series, String tokenValue, Date lastUsed) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.UPDATE_REMEMBER_USER_TOKEN").getSql(), 
				new SqlParameterValue(Types.VARCHAR, tokenValue), 
				new SqlParameterValue(Types.TIMESTAMP, lastUsed), 
				new SqlParameterValue(Types.VARCHAR, series));		
	}
 
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		try {
			return getJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.SELECT_REMEMBER_USER_TOKEN_BY_UUID").getSql(),
					new RowMapper<PersistentRememberMeToken>() {
						public PersistentRememberMeToken mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));
						}
					}, new SqlParameterValue(Types.VARCHAR, seriesId ));
		}
		catch (EmptyResultDataAccessException zeroResults) {
			if (logger.isDebugEnabled()) {
				logger.debug("Querying token for series '" + seriesId + "' returned no results.", zeroResults);
			}
		}
		catch (IncorrectResultSizeDataAccessException moreThanOne) {
			logger.error("Querying token for series '" + seriesId + "' returned more than one value. Series" + " should be unique");
		}
		catch (DataAccessException e) {
			logger.error("Failed to load token for series " + seriesId, e);
		}
		return null;
	}

	public void removeUserTokens(String username) {
		getJdbcTemplate().update(getBoundSql("COMMUNITY_USER.DELETE_REMEMBER_USER_TOKEN_BY_USERNAME").getSql(), 
				new SqlParameterValue(Types.VARCHAR, username));
	}

}
