package architecture.community.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.model.Models;
import architecture.community.user.DefaultRole;
import architecture.community.user.Role;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

//@Repository("roleDao")
//@MaxValue(id=Role.MODLE_TYPE, name="ROLE")
public class JdbcRoleDao extends ExtendedJdbcDaoSupport implements RoleDao {

	private final RowMapper<Role> roleMapper = new RowMapper<Role>() {
		public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultRole model = new DefaultRole();
			model.setRoleId(rs.getLong("ROLE_ID"));
			model.setName(rs.getString("NAME"));
			model.setDescription(rs.getString("DESCRIPTION"));
			model.setCreationDate(rs.getDate("CREATION_DATE"));
			model.setModifiedDate(rs.getDate("MODIFIED_DATE"));
			return (Role) model;
		}
	};

	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	public JdbcRoleDao() {
		super();
	}

	public long getNextRoleId() {
		return sequencerFactory.getNextValue(Models.ROLE.getObjectType(), Models.ROLE.name());
	}

	@Override
	public void createRole(Role role) {		
		DefaultRole roleToUse = (DefaultRole)role;
		if (roleToUse.getName() == null)
			throw new IllegalArgumentException();		

		if ("".equals(roleToUse.getDescription()))
			roleToUse.setDescription(null);
		
		if( roleToUse.getRoleId() < 1 ){
			roleToUse.setRoleId(getNextRoleId());
		}		
		try {
			Date now = new Date();
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.CREATE_ROLE").getSql(),
					new SqlParameterValue(Types.NUMERIC, roleToUse.getRoleId()),
					new SqlParameterValue(Types.VARCHAR, roleToUse.getName()),
					new SqlParameterValue(Types.VARCHAR, roleToUse.getDescription()),
					new SqlParameterValue(Types.TIMESTAMP, roleToUse.getCreationDate() != null ? roleToUse.getCreationDate() : now ),
					new SqlParameterValue(Types.TIMESTAMP, roleToUse.getModifiedDate() != null ? roleToUse.getModifiedDate() : now )
			);
		} catch (DataAccessException e) {
			//logger.error(CommunityLogLocalizer.getMessage("010013"), e);
			throw e;
		}		
	}

	@Override
	public void updateRole(Role role) {
		Date now = new Date();
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.UPDATE_ROLE").getSql(),
				new SqlParameterValue(Types.VARCHAR, role.getName()),
				new SqlParameterValue(Types.VARCHAR, role.getDescription()),
				new SqlParameterValue(Types.TIMESTAMP, role.getModifiedDate() != null ? role.getModifiedDate() : now ),
				new SqlParameterValue(Types.NUMERIC, role.getRoleId()));
	}

	@Override
	public void deleteRole(Role role) {
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.DELETE_ALL_USER_ROLES").getSql(), new SqlParameterValue(Types.NUMERIC, role.getRoleId()));
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.DELETE_ROLE").getSql(), new SqlParameterValue(Types.NUMERIC, role.getRoleId()));
		
	}

	public Role getRoleById(long roleId) {
		Role role = null;
		try {
			role = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.SELECT_ROLE_BY_ID").getSql(),
					roleMapper, new SqlParameterValue(Types.NUMERIC, roleId));
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() > 1) {
				logger.warn(CommunityLogLocalizer.format("010203", roleId));
				throw e;
			}
		} catch (DataAccessException e) {
			logger.warn(CommunityLogLocalizer.format("010204", roleId));
		}
		return role;
	}


	public Role getRoleByName(String name, boolean caseSensetive) {
		Role role = null;
		try {
			role = getExtendedJdbcTemplate().queryForObject(getBoundSqlWithAdditionalParameter("COMMUNITY_USER.SELECT_ROLE_BY_NAME", caseSensetive).getSql(),
					roleMapper, new SqlParameterValue(Types.VARCHAR, name));
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() > 1) {
				logger.warn(CommunityLogLocalizer.format("010203", name));
				throw e;
			}
		} catch (DataAccessException e) {
			logger.warn(CommunityLogLocalizer.format("010204", name));
		}
		return role;
	}

	@Override
	public List<Long> getAllRoleIds() {
		return getExtendedJdbcTemplate().queryForList(getBoundSql("COMMUNITY_USER.SELECT_ALL_ROLE_IDS").getSql(), Long.class);
	}

	@Override
	public int getRoleCount() {
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.COUNT_ALL_ROLES").getSql(), Integer.class);
	}

	@Override
	public List<Long> getUserRoleIds(long userId) {
		return getExtendedJdbcTemplate().queryForList(getBoundSql("COMMUNITY_USER.SELECT_USER_ROLE_IDS").getSql(), Long.class, new SqlParameterValue(Types.NUMERIC, userId));
	}

	@Override
	public void removeUserRoles(long userId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.REMOVE_USER_ROLES").getSql(), new SqlParameterValue(Types.NUMERIC, userId));
	}
	
	@Override
	public void removeUserRole(long roleId, long userId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.REMOVE_USER_ROLE").getSql(), new SqlParameterValue(Types.NUMERIC, roleId), new SqlParameterValue(Types.NUMERIC, userId));
	}

	@Override
	public void addUserRole(long roleId, long userId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.ADD_USER_ROLE").getSql(), new SqlParameterValue(Types.NUMERIC, roleId), new SqlParameterValue(Types.NUMERIC, userId));
	}

}
