package architecture.community.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.DataSourceRequest.FilterDescriptor;
import architecture.ee.jdbc.sqlquery.mapping.BoundSql;

public class Utils {
	
	
	public static Map<String, Object> getAdditionalParameter( DataSourceRequest dataSourceRequest ){
		Map<String, Object> additionalParameter = new HashMap<String, Object>();
		additionalParameter.put("filter", dataSourceRequest.getFilter());
		additionalParameter.put("sort", dataSourceRequest.getSort());		
		additionalParameter.put("data", dataSourceRequest.getData());
		return additionalParameter;
	}
	
	
	public static SqlParameterValue newSqlParameterValue(int sqlType, Object value) {
		return new SqlParameterValue(sqlType, value);
	}
	
	public static List<SqlParameterValue> getSqlParameterValues (List<ParameterValue> values ){
		ArrayList<SqlParameterValue> al = new ArrayList<SqlParameterValue>();	
		for( ParameterValue v : values)
		{
			al.add(new SqlParameterValue(v.getJdbcType(), v.isSetByObject() ? v.getValueObject() : v.getValueText()) );
		}
		return al;
	}
	

	public static <T> List<T> list(CustomQueryJdbcDao dao, DataSourceRequest request, Class<T> elementType) {	
		BoundSql sqlSource = dao.getBoundSqlWithAdditionalParameter(request.getStatement(), getAdditionalParameter(request));		
		if( request.getPageSize() > 0 ) {
			// paging 
			if( request.getParameters().size() > 0 ) 
				return dao.getExtendedJdbcTemplate().query( 
					sqlSource.getSql(), 
					request.getSkip(), 
					request.getPageSize(),
					elementType,
					architecture.community.query.Utils.getSqlParameterValues( request.getParameters() ).toArray());
			else 
				return dao.getExtendedJdbcTemplate().query( 
					sqlSource.getSql(), 
					request.getSkip(), 
					request.getPageSize(),
					elementType);
			
		}else {		
			if( request.getParameters().size() > 0 )
				return dao.getExtendedJdbcTemplate().queryForList(sqlSource.getSql(), elementType, getSqlParameterValues( request.getParameters() ).toArray() );
			else	
				return dao.getExtendedJdbcTemplate().queryForList(sqlSource.getSql(), elementType);
		}
	}
	
	public static <T> T queryForObject (CustomQueryJdbcDao dao, DataSourceRequest request, Class<T> requiredType) {				
		BoundSql sqlSource = dao.getBoundSqlWithAdditionalParameter(request.getStatement(), getAdditionalParameter(request));		
		if( request.getParameters().size() > 0 )
			return dao.getExtendedJdbcTemplate().queryForObject( sqlSource.getSql(), requiredType, getSqlParameterValues( request.getParameters() ).toArray());
		else	
			return dao.getExtendedJdbcTemplate().queryForObject( sqlSource.getSql(), requiredType );
	}
	
	

	public static String getRestrictionExpression(FilterDescriptor filter) { 
		
		String operation = filter.getOperator();
		String field = filter.getField();
		Object value = filter.getValue();
		boolean ignoreCase = filter.isIgnoreCase();
		String[] nullables = {"isnull", "isnotnull" , "isempty", "isnotempty"};
		
		if(StringUtils.isNotEmpty(operation) ) {
			if( !Arrays.asList(nullables).contains(operation) ) {}	
		} 
		StringBuilder sb = new StringBuilder();
		
		if( StringUtils.isNotEmpty( filter.getLogic() ))
			sb.append( filter.getLogic() ).append(" ");
		
		switch(operation)
		{
		case "eq" :
			sb.append(field).append(" ").append("=").append(" ").append("'").append(value.toString()).append("'") ;
			break;
		case "neq":
			sb.append(field).append(" ").append("!=").append(" ").append("'").append(value.toString()).append("'") ;
			break;
		case "gt" :	
			sb.append(field).append(" ").append(">").append(" ").append("'").append(value.toString()).append("'") ;
			break;
		case "gte" : 
			sb.append(field).append(" ").append(">=").append(" ").append("'").append(value.toString()).append("'") ;
			break;
		case "lt" :
			sb.append(field).append(" ").append("<").append(" ").append("'").append(value.toString()).append("'") ;
			break;
		case "lte" :
			sb.append(field).append(" ").append("<=").append(" ").append("'").append(value.toString()).append("'") ;
			break;
		case "startswith" :
			sb.append(field).append(" ").append("LIKE").append(" ").append(" '").append(value.toString()).append("%'") ;
			break;
		case "endswith" :
			sb.append(field).append(" ").append("LIKE").append(" ").append(" '%").append(value.toString()).append("'") ;
			break;
		case "contains" :
			sb.append(field).append(" ").append("LIKE").append(" ").append(" '%").append(value.toString()).append("%'") ;
			break;
		case "doesnotcontain" :
			sb.append(field).append(" ").append("NOT LIKE").append(" ").append(" '%").append(value.toString()).append("%'") ;
			break;
		case "isnull" :
			sb.append(field).append(" ").append("IS NULL").append(" ");
			break;
		case "isnotnull" : 
			sb.append(field).append(" ").append("IS NOT NULL").append(" ");
			break;
		case "isempty" :
			break;
		case "isnotempty" : 
			break;
		} 
		return sb.toString();
	}
}
