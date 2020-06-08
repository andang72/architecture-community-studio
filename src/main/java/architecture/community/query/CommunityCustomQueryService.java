package architecture.community.query;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.util.CommunityContextHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.mapping.BoundSql;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class CommunityCustomQueryService implements CustomQueryService {

	private Logger logger = LoggerFactory.getLogger(getClass()) ;
	
	@Autowired
	@Qualifier("sqlConfiguration")
	private Configuration sqlConfiguration;

	@Autowired
	@Qualifier("customQueryJdbcDao")
	private CustomQueryJdbcDao customQueryJdbcDao;
	
	
	public CommunityCustomQueryService() {		
	}

	public CommunityCustomQueryService(CustomQueryJdbcDao customQueryJdbcDao) {
		this.customQueryJdbcDao = customQueryJdbcDao;
	}
	
	public CustomQueryJdbcDao getCustomQueryJdbcDao() {
		return customQueryJdbcDao;
	}

	public void setCustomQueryJdbcDao(CustomQueryJdbcDao customQueryJdbcDao) {
		this.customQueryJdbcDao = customQueryJdbcDao;
	}

	public List<Map<String, Object>> list(String statement) {
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.setStatement(statement);
		return list(dataSourceRequest, getColumnMapRowMapper());
	}
	
 
	public <T> List<T> list(String statement, RowMapper<T> rowmapper) {
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.setStatement(statement);
		return list(dataSourceRequest, rowmapper );
	}
	
	
	public List<Map<String, Object>> list(String statement, Map<String, Object> data) {
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.setStatement(statement);
		for( String name : data.keySet() ) {
			dataSourceRequest.setData(name, data.get(name));
		}
		return list(dataSourceRequest, getColumnMapRowMapper());
	}	
	
	public String queryForString(String statement){
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.setStatement(statement);
		return queryForObject( dataSourceRequest, String.class);
	}
	
	public <T> T queryForObject (DataSourceRequest dataSourceRequest, Class<T> requiredType) {				
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));		
		if( dataSourceRequest.getParameters().size() > 0 )
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForObject( sqlSource.getSql(), requiredType, getSqlParameterValues( dataSourceRequest.getParameters() ).toArray());
		else	
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForObject( sqlSource.getSql(), requiredType );
	}
	
	public <T> T queryForObject (DataSourceRequest dataSourceRequest, RowMapper<T> rowmapper) {			
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));
		if( dataSourceRequest.getParameters().size() > 0 )
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForObject( sqlSource.getSql(), rowmapper, getSqlParameterValues( dataSourceRequest.getParameters() ).toArray() );
		else	
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForObject( sqlSource.getSql(), rowmapper );
	}
	
	public Map<String, Object> queryForMap (DataSourceRequest dataSourceRequest) {				
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));		
		if( dataSourceRequest.getParameters().size() > 0 )
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForMap( sqlSource.getSql(), getSqlParameterValues( dataSourceRequest.getParameters() ).toArray() );
		else	
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForMap( sqlSource.getSql() );
	}

	public List<Map<String, Object>> list( DataSourceRequest dataSourceRequest ) {
		return list(dataSourceRequest, getColumnMapRowMapper());
	}
	
	public <T> List<T> list(DataSourceRequest dataSourceRequest, RowMapper<T> rowmapper) {			
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));
		if( dataSourceRequest.getPageSize() > 0 ){	
			if( dataSourceRequest.getParameters().size() > 0 )
				return customQueryJdbcDao.getExtendedJdbcTemplate().query( sqlSource.getSql(), dataSourceRequest.getSkip(),  dataSourceRequest.getPageSize(), rowmapper , getSqlParameterValues( dataSourceRequest.getParameters() ).toArray() );		
			else
				return customQueryJdbcDao.getExtendedJdbcTemplate().query( sqlSource.getSql(), dataSourceRequest.getSkip(),  dataSourceRequest.getPageSize(), rowmapper );					
		}else {
			if( dataSourceRequest.getParameters().size() > 0 )
				return customQueryJdbcDao.getExtendedJdbcTemplate().query(sqlSource.getSql(), rowmapper, getSqlParameterValues( dataSourceRequest.getParameters() ) );
			else	
				return customQueryJdbcDao.getExtendedJdbcTemplate().query(sqlSource.getSql(), rowmapper );
		}
	}
	
	public <T> T list(DataSourceRequest dataSourceRequest, ResultSetExtractor<T> extractor) {				
		logger.debug("Paging not support yet.");		
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));
		if( dataSourceRequest.getParameters().size() > 0 )
			return customQueryJdbcDao.getExtendedJdbcTemplate().query(sqlSource.getSql(), extractor, getSqlParameterValues( dataSourceRequest.getParameters() ).toArray() );
		else	
			return customQueryJdbcDao.getExtendedJdbcTemplate().query(sqlSource.getSql(), extractor );
	}	
 
	
	public void query(DataSourceRequest dataSourceRequest, RowCallbackHandler callback) {	
		logger.debug("Paging not support.");		
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));
		if( dataSourceRequest.getParameters().size() > 0 )
			customQueryJdbcDao.getExtendedJdbcTemplate().query(sqlSource.getSql(), callback, getSqlParameterValues( dataSourceRequest.getParameters() ).toArray() );
		else	
			customQueryJdbcDao.getExtendedJdbcTemplate().query(sqlSource.getSql(), callback );		
	}	
	
	public <T> List<T> list(DataSourceRequest dataSourceRequest, Class<T> elementType) {	
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));		
		if( dataSourceRequest.getPageSize() > 0 ) {
			// paging 
			if( dataSourceRequest.getParameters().size() > 0 ) 
				return customQueryJdbcDao.getExtendedJdbcTemplate().query( 
					sqlSource.getSql(), 
					dataSourceRequest.getSkip(), 
					dataSourceRequest.getPageSize(),
					elementType,
					getSqlParameterValues( dataSourceRequest.getParameters() ).toArray());
			else 
				return customQueryJdbcDao.getExtendedJdbcTemplate().query( 
					sqlSource.getSql(), 
					dataSourceRequest.getSkip(), 
					dataSourceRequest.getPageSize(),
					elementType);
			
		}else {		
			if( dataSourceRequest.getParameters().size() > 0 )
				return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(sqlSource.getSql(), elementType, getSqlParameterValues( dataSourceRequest.getParameters() ).toArray() );
			else	
				return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(sqlSource.getSql(), elementType);
		}
	}
	
	
	public <T> List<T> list(String statement, List<ParameterValue> values, Class<T> elementType) {
		if (values.size() > 0)
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(customQueryJdbcDao.getBoundSql(statement).getSql(), elementType, getSqlParameterValues(values).toArray());
		else
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(customQueryJdbcDao.getBoundSql(statement).getSql(), elementType);
	}	
	

	public List<Map<String, Object>> list( String statement, List<ParameterValue> values) {
		if (values.size() > 0)
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(customQueryJdbcDao.getBoundSql(statement).getSql(), getSqlParameterValues(values).toArray());
		else
			return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(customQueryJdbcDao.getBoundSql(statement).getSql());
	}
	 
	public List<Map<String, Object>> listByValue( String statement, Object value) {
		return customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(customQueryJdbcDao.getBoundSql(statement).getSql(), value ); 
	} 
	
	public <T> List<T> list( String statement, List<ParameterValue> values, RowMapper<T> rowmapper) {
		if (values.size() > 0)
			return customQueryJdbcDao.getExtendedJdbcTemplate().query(customQueryJdbcDao.getBoundSql(statement).getSql(), rowmapper, getSqlParameterValues(values).toArray());
		else
			return customQueryJdbcDao.getExtendedJdbcTemplate().query(customQueryJdbcDao.getBoundSql(statement).getSql(), rowmapper);
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int update ( String statement , Object... args) {
		return customQueryJdbcDao.getExtendedJdbcTemplate().update(customQueryJdbcDao.getBoundSql(statement).getSql(), args);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int update ( DataSourceRequest dataSourceRequest ) {
		BoundSql sqlSource = customQueryJdbcDao.getBoundSqlWithAdditionalParameter(dataSourceRequest.getStatement(), getAdditionalParameter(dataSourceRequest));
		if( dataSourceRequest.getParameters().size() > 0 ) 
			return customQueryJdbcDao.getExtendedJdbcTemplate().update(  sqlSource.getSql(),  getSqlParameterValues( dataSourceRequest.getParameters() ).toArray());
		else 
			return customQueryJdbcDao.getExtendedJdbcTemplate().update( sqlSource.getSql()); 
	}	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public <T> T execute(CustomTransactionCallback<T> action) throws DataAccessException {
		Assert.notNull(action, "Callback object must not be null");
		T result = action.doInTransaction(customQueryJdbcDao);
		return result;
	}	
	
	
	/**
	 * 
	 */
	public List<Map<String, Object>> list(String source, String statement, List<ParameterValue> values) {
		DataSource dataSource = CommunityContextHelper.getComponent(source, DataSource.class);
		ExtendedJdbcDaoSupport dao = new ExtendedJdbcDaoSupport(sqlConfiguration);
		dao.setDataSource(dataSource);
		if (values.size() > 0)
			return dao.getExtendedJdbcTemplate().queryForList(dao.getBoundSql(statement).getSql(), getSqlParameterValues(values).toArray());
		else
			return dao.getExtendedJdbcTemplate().queryForList(dao.getBoundSql(statement).getSql());
	}
	
	/**
	 * 외부에서 전달된 인자들을 스프링이 인식하는 형식의 값을 변경하여 처리한다.
	 * @param values
	 * @return
	 */
	private List<SqlParameterValue> getSqlParameterValues (List<ParameterValue> values ){
		return Utils.getSqlParameterValues(values);
	}
	
	/**
	 * 다이나믹 쿼리 처리를 위하여 필요한 파라메터들을 Map 형식의 데이터로 생성한다. 
	 * @param dataSourceRequest
	 * @return
	 */
	protected Map<String, Object> getAdditionalParameter( DataSourceRequest dataSourceRequest ){
		return Utils.getAdditionalParameter(dataSourceRequest);
	}

	protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
		return new ColumnMapRowMapper();
	}


}
