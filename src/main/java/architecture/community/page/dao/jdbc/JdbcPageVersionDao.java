package architecture.community.page.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.page.DefaultPage;
import architecture.community.page.DefaultPageVersion;
import architecture.community.page.PageState;
import architecture.community.page.PageVersion;
import architecture.community.page.dao.PageVersionDao;
import architecture.community.user.UserTemplate;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcPageVersionDao extends ExtendedJdbcDaoSupport implements PageVersionDao {

	private final RowMapper<PageVersion> pageVersionMapper = new RowMapper<PageVersion>() {
		public PageVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultPageVersion version = new DefaultPageVersion();
			version.setPage(new DefaultPage(rs.getLong("PAGE_ID")));
			version.setVersionNumber(rs.getInt("VERSION_ID"));
			version.setPageState(PageState.valueOf(rs.getString("STATE").toUpperCase()));
			version.setCreationDate(rs.getDate("CREATION_DATE"));
			version.setModifiedDate(rs.getDate("MODIFIED_DATE"));
			version.setAuthor(new UserTemplate(rs.getLong("USER_ID")));
			return version;
		}
	};

	public void update(PageVersion pageVersion) {
		if (pageVersion.getPageState() == PageState.PUBLISHED) {
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_STATE_TO_ARCHIVED").getSql(),
					new SqlParameterValue(Types.NUMERIC, pageVersion.getPage().getPageId()),
					new SqlParameterValue(Types.NUMERIC, pageVersion.getVersionNumber()));
			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_PAGE_STATE").getSql(),
					new SqlParameterValue(Types.NUMERIC, pageVersion.getAuthor()),
					new SqlParameterValue(Types.DATE, pageVersion.getCreationDate()),
					new SqlParameterValue(Types.DATE, pageVersion.getModifiedDate()),
					new SqlParameterValue(Types.VARCHAR, pageVersion.getPageState().name().toLowerCase()),
					new SqlParameterValue(Types.NUMERIC, pageVersion.getPage().getPageId()),
					new SqlParameterValue(Types.NUMERIC, pageVersion.getVersionNumber()));
		}
	}

	public void delete(PageVersion pageVersion) {

	}

	public PageVersion getPageVersion(long pageId, int versionNumber) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_BY_ID_AND_VERSION").getSql(), pageVersionMapper,
				new SqlParameterValue(Types.NUMERIC, pageId), new SqlParameterValue(Types.NUMERIC, versionNumber));
	}

	public List<PageVersion> getPageVersions(long pageId) {
		return getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_VERSIONS").getSql(),
				pageVersionMapper, new SqlParameterValue(Types.NUMERIC, pageId));
	}

	public List<Integer> getPageVersionIds(long pageId) {
		return getExtendedJdbcTemplate().queryForList(getBoundSql("COMMUNITY_PAGE.SELECT_PAGE_VERSION_IDS").getSql(),
				Integer.class, new SqlParameterValue(Types.NUMERIC, pageId));
	}
}
