package architecture.community.query;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.web.model.DataSourceRequest;

public interface CustomQueryService {

	public List<Map<String, Object>> list( String statement ) ;	 
	
	public <T> List<T> list( String statement ,  RowMapper<T> rowmapper);
	
	public List<Map<String, Object>> list( String statement , Map<String, Object> data) ;	
	
	/**
	 *  
	 * API using DataSourceRequest 
	 *
	 */
	
	public List<Map<String, Object>> list( DataSourceRequest dataSourceRequest ) ;	
	/**
	 * 
	 * @param dataSourceRequest
	 * @param rowmapper Row 단위 결과 데이터 처리자 
	 * @return
	 */
	public <T> List<T> list( DataSourceRequest dataSourceRequest, RowMapper<T> rowmapper);
	
	
	/**
	 * 
	 * @param dataSourceRequest
	 * @param extractor 결과 데이터 처리자 
	 * @return
	 */
	public <T> T list(DataSourceRequest dataSourceRequest, ResultSetExtractor<T> extractor) ;
	 
	/**
	 * 이 함수는 단일 컬럼에 대한 결과를 List 형태로 리턴한다. 
	 *  
	 * @param dataSourceRequest
	 * @param elementType 결과 데이터 타입  
	 * @return
	 */
	public <T> List<T> list(DataSourceRequest dataSourceRequest, Class<T> elementType);
	
	public abstract String queryForString(String statement);
	
	public <T> T queryForObject (DataSourceRequest dataSourceRequest, Class<T> requiredType);
	
	public <T> T queryForObject (DataSourceRequest dataSourceRequest, RowMapper<T> rowmapper);
	
	public Map<String, Object> queryForMap (DataSourceRequest dataSourceRequest);
	
	public int update ( DataSourceRequest dataSourceRequest ) ;

	
	/**
	 * API directly access Spring JdbcTemplate Function .
	 */
	public void query(DataSourceRequest dataSourceRequest, RowCallbackHandler callback);
	
	
	
	/**
	 *  
	 * Old style API
	 *
	 */
	
	/**
	 * 
	 * @param statement 쿼리 키 
	 * @param values 파라메터 값 
	 * @param rowmapper 결과 데이터처 처리자 
	 * @return
	 */
	public <T> List<T> list( String statement, List<ParameterValue> values, RowMapper<T> rowmapper);
	
	public <T> List<T> list(String statement, List<ParameterValue> values, Class<T> elementType);
	
	
	public List<Map<String, Object>> listByValue( String statement, Object value);
	
	
	/**
	 * 
	 * @param statement 쿼리 키
	 * @param values 파라메터 값 
	 * @return 
	 */
	public List<Map<String, Object>> list(String statement, List<ParameterValue> values) ;

	/**
	 * 
	 * @param source 데이터소스 이름 
	 * @param statement 쿼리 키
	 * @param values 파라메터 값
	 * @return
	 */
	public List<Map<String, Object>> list(String source, String statement, List<ParameterValue> values) ;
	
	public abstract int update ( String statement , Object... args) ;
	
	public abstract <T> T execute(CustomTransactionCallback<T> callback ) throws  DataAccessException;	
		
	public abstract CustomQueryJdbcDao getCustomQueryJdbcDao(); 
	
}
