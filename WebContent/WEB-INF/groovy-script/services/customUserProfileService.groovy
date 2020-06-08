package architecture.community.user.profile.groovy;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.RowMapper;

import architecture.community.query.Utils;
import architecture.community.user.UserProfile;
import architecture.community.user.profile.CustomUserProfile;
import architecture.community.user.profile.AbstractUserProfileService;

public class CustomUserProfileService extends AbstractUserProfileService {
	
	protected UserProfile loadUserProfile(Long userId) throws Exception {
		if(isEnabled()) {
			return customQueryJdbcDao.getJdbcTemplate().queryForObject(
				"SELECT * FROM TB_USER_PROFILE WHERE USER_ID = ?",
				new RowMapper<CustomUserProfile>(){
					public CustomUserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
						CustomUserProfile profile = new CustomUserProfile(userId); 
						profile.data.put("ZIP_OR_POSTALCODE", rs.getString("ZIP_OR_POSTALCODE"));
						profile.data.put("OFFICE_ADDRESS_1", rs.getString("OFFICE_ADDRESS_1"));
						profile.data.put("OFFICE_ADDRESS_2", rs.getString("OFFICE_ADDRESS_2"));
						profile.data.put("FAX_NUM", rs.getString("FAX_NUM"));
						profile.data.put("PHONE_NUM", rs.getString("PHONE_NUM"));
						profile.data.put("CREATION_DATE", rs.getDate("CREATION_DATE"));
						profile.data.put("MODIFIED_DATE", rs.getDate("MODIFIED_DATE")); 
						return profile;
					}
				},
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