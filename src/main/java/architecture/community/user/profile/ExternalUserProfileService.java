package architecture.community.user.profile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.RowMapper;

import architecture.community.query.Utils;
import architecture.community.user.UserProfile;

public class ExternalUserProfileService extends AbstractUserProfileService { 
	protected UserProfile loadUserProfile(Long userId) throws Exception {
		if(isEnabled()) { 
			customQueryJdbcDao.getJdbcTemplate().queryForObject(
				"SELECT * FROM DUAL", 
				new RowMapper<CustomUserProfile>(){ 
					public CustomUserProfile mapRow(ResultSet rs, int rowNum) throws SQLException { 
						return new CustomUserProfile(userId);
					}}, 
				Utils.newSqlParameterValue(Types.NUMERIC, userId));
		}
		return null;
	}
 
	protected void saveOrUpdate(UserProfile profile) {
		if(isEnabled()) {
			customQueryJdbcDao.getJdbcTemplate().update(
				"", 
				Utils.newSqlParameterValue(Types.NUMERIC, profile.getUserId())); 
			
		} 
	}  
}


