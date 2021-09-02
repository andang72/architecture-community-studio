package architecture.community.page.api.dao.jdbc;

import java.sql.PreparedStatement;
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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.model.Models;
import architecture.community.page.Parameter;
import architecture.community.page.api.Api;
import architecture.community.page.api.dao.ApiDao;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcApiDao extends ExtendedJdbcDaoSupport implements ApiDao  {

	private final RowMapper<Api> apiMapper = new RowMapper<Api>() {		
		
		public Api mapRow(ResultSet rs, int rowNum) throws SQLException {			
			Api api = new Api(rs.getInt("OBJECT_TYPE"), rs.getLong("OBJECT_ID"), rs.getLong("API_ID"));	
			api.setTitle(rs.getString("TITLE"));
			api.setPattern(rs.getString("PATTERN"));
			api.setName(rs.getString("API_NAME"));
			api.setVersion(rs.getString("API_VERSION"));
			api.setDescription(rs.getString("DESCRIPTION"));
			api.setContentType(rs.getString("CONTENT_TYPE"));
			api.setScriptSource(rs.getString("SCRIPT"));
			api.setSecured(rs.getInt("SECURED") == 1);
			api.setEnabled(rs.getInt("ENABLED") == 1);
			api.setCreator(new UserTemplate(rs.getLong("CREATOR_ID")));
			api.setCreationDate(rs.getDate("CREATION_DATE"));
			api.setModifiedDate(rs.getDate("MODIFIED_DATE")); 
			return api;
		}		
	};
	
	private final RowMapper<Parameter> parameterMapper = new RowMapper<Parameter>() {		
		
		public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {		 
			Parameter param = new Parameter(
				rs.getInt("OBJECT_TYPE"), 
				rs.getLong("OBJECT_ID"),
				rs.getInt("IS_HEADER") == 1,
				rs.getInt("IS_REQUESTPARAM") == 1,
				rs.getInt("IS_PATHVARIABLE") == 1,
				rs.getString("PARAM_KEY"),
				rs.getString("PARAM_VALUE"),
				rs.getString("DEFAULT_VALUE")
			); 
			param.setCreationDate(rs.getDate("CREATION_DATE"));
			param.setModifiedDate(rs.getDate("MODIFIED_DATE")); 
			return param;
		}		
	};
	
	
	public JdbcApiDao() { 
		
	}

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	
	private String apiPropertyTableName = "AC_UI_API_PROPERTY";
	
	private String apiPropertyPrimaryColumnName = "API_ID";
	
	
	public long getNextApiId(){
		logger.debug("next id for {}, {}", Models.API.getObjectType(), Models.API.name() );
		return sequencerFactory.getNextValue(Models.API.getObjectType(), Models.API.name());
	}

	public Map<String, String> getApiProperties(long apiId) {
		return propertyDao.getProperties(apiPropertyTableName, apiPropertyPrimaryColumnName, apiId);
	}

	public void deleteApiProperties(long apiId) {
		propertyDao.deleteProperties(apiPropertyTableName, apiPropertyPrimaryColumnName, apiId);
	}
	
	public void setApiProperties(long apiId, Map<String, String> props) {
		propertyDao.updateProperties(apiPropertyTableName, apiPropertyPrimaryColumnName, apiId, props);
	}	
	
	
	public List<Parameter> getApiHTTPParameters(Api api) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_PAGE.SELECT_HTTP_PARAMETERS").getSql(),
				parameterMapper,
				new SqlParameterValue(Types.NUMERIC, Models.API.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, api.getApiId()));
	}
	
	
	public void deleteApiHTTPParameters(Api api) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_HTTP_PARAMETERS").getSql(), 
				new SqlParameterValue(Types.NUMERIC, Models.API.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, api.getApiId())
		);
	}
	
	
	public void setApiHTTPParameters( Api api ) {  
		
		final List<Parameter> list = api.getParameters();
		final long apiId = api.getApiId();
		
		getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_PAGE.INSERT_HTTP_PARAMETER").getSql(), new BatchPreparedStatementSetter() { 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
			
				
				ps.setInt(1, Models.API.getObjectType());
				ps.setLong(2, apiId );
				ps.setInt( 3, list.get(i).isHeader() ? 1 : 0 );
				ps.setInt( 4, list.get(i).isRequestParam() ? 1 : 0 );
				ps.setInt( 5, list.get(i).isPathVariable() ? 1 : 0 );
				ps.setString(6, list.get(i).getKey());
				ps.setString(7, list.get(i).getValue());
				ps.setString(8, list.get(i).getDefaultValue());
				ps.setTimestamp(9, new java.sql.Timestamp( list.get(i).getCreationDate().getTime() ));
				ps.setTimestamp(10, new java.sql.Timestamp( list.get(i).getModifiedDate().getTime() ));	}
			
			
			public int getBatchSize() { 
				return list.size();
			}
			
		});
		
	}
	
	@Override
	public void saveOrUpdate(Api api) { 
		Api toUse = api;
		if (toUse.getApiId() < 1L) {
			toUse.setApiId(getNextApiId());		
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.INSERT_API").getSql(),
					new SqlParameterValue(Types.NUMERIC, toUse.getObjectType()),
					new SqlParameterValue(Types.NUMERIC, toUse.getObjectId()),					
					new SqlParameterValue(Types.NUMERIC, toUse.getApiId()),
					new SqlParameterValue(Types.VARCHAR, toUse.getTitle()),
					new SqlParameterValue(Types.VARCHAR, toUse.getName()),
					new SqlParameterValue(Types.VARCHAR, toUse.getVersion()),
					new SqlParameterValue(Types.VARCHAR, toUse.getDescription()),
					new SqlParameterValue(Types.VARCHAR, toUse.getContentType()),
					new SqlParameterValue(Types.VARCHAR, toUse.getScriptSource()),		
					new SqlParameterValue(Types.VARCHAR, toUse.getPattern()),		
					new SqlParameterValue(Types.NUMERIC, toUse.isSecured() ? 1 : 0 ),
					new SqlParameterValue(Types.NUMERIC, toUse.isEnabled() ? 1 : 0 ),
					new SqlParameterValue(Types.NUMERIC, toUse.getCreator().getUserId()),
					new SqlParameterValue(Types.TIMESTAMP, toUse.getCreationDate()),
					new SqlParameterValue(Types.TIMESTAMP, toUse.getModifiedDate()));  
			deleteApiHTTPParameters(toUse);
		} else {
			Date now = Calendar.getInstance().getTime();
			toUse.setModifiedDate(now);		
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.UPDATE_API").getSql(),
					new SqlParameterValue(Types.VARCHAR, toUse.getTitle()),
					new SqlParameterValue(Types.VARCHAR, toUse.getName()),
					new SqlParameterValue(Types.VARCHAR, toUse.getVersion()),
					new SqlParameterValue(Types.VARCHAR, toUse.getDescription()),
					new SqlParameterValue(Types.VARCHAR, toUse.getContentType()),
					new SqlParameterValue(Types.VARCHAR, toUse.getScriptSource()),
					new SqlParameterValue(Types.VARCHAR, toUse.getPattern()),
					new SqlParameterValue(Types.NUMERIC, toUse.isSecured() ? 1 : 0 ),
					new SqlParameterValue(Types.NUMERIC, toUse.isEnabled() ? 1 : 0 ),
					new SqlParameterValue(Types.TIMESTAMP, toUse.getModifiedDate()),
					new SqlParameterValue(Types.NUMERIC, toUse.getApiId())
			); 
			
		}			
		setApiHTTPParameters(toUse);
	}
 
	public Long getApiIdByName(String name) { 
		Long id = -1L;
		id = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_PAGE.SELECT_API_ID_BY_NAME").getSql(), Long.class, new SqlParameterValue(Types.VARCHAR, name ) );
		return id;
	}
 
	public Api getApiById(long apiId) {
		try {
			Api api = getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_PAGE.SELECT_API_BY_ID").getSql(),
				apiMapper,
				new SqlParameterValue(Types.NUMERIC, apiId));
			
			Map<String, String> properties = getApiProperties(api.getApiId());
			api.getProperties().putAll(properties);
			api.setParameters( getApiHTTPParameters(api) );
			
			return api;
		} catch (DataAccessException e) {
			logger.warn(e.getMessage());
			return null;
		}
	}
 
	public void deleteApi(Api api) {
		Api toUse = api;
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_PAGE.DELETE_API_BY_ID").getSql(), 
				new SqlParameterValue(Types.NUMERIC, toUse.getApiId())
		);
		deleteApiHTTPParameters(api);
	}
 
	public List<Api> getAllApiHasPatterns() {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_PAGE.SELECT_ALL_API_PATTERN_AND_ID").getSql(),
				new RowMapper<Api>() {
					public Api mapRow(ResultSet rs, int rowNum) throws SQLException {
						Api page = new Api(rs.getLong(1));
						page.setPattern(rs.getString(2));
						return page;
					}
				});
	}

}
