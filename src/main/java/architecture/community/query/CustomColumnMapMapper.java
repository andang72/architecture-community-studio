package architecture.community.query;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import architecture.ee.jdbc.sqlquery.mapping.JdbcType;
import architecture.ee.jdbc.sqlquery.mapping.ParameterMapping;
import architecture.ee.util.StringUtils;

public class CustomColumnMapMapper extends ColumnMapRowMapper {

	private Logger log = LoggerFactory.getLogger(CustomColumnMapMapper.class);
	
	private List<ParameterMapping> parameterMappings;
	
	public CustomColumnMapMapper(List<ParameterMapping> parameterMappings) {
		this.parameterMappings = parameterMappings;
	}

	@Override
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		for(ParameterMapping mapping : parameterMappings){	
			int indexToUse = mapping.getIndex();
			/*
			log.debug("compare {} with {} is {} ",
				mapping.getColumn() , 
				JdbcUtils.lookupColumnName(rsmd, index), 
				org.apache.commons.codec.binary.StringUtils.equals( mapping.getColumn(), JdbcUtils.lookupColumnName(rsmd, index) ) 
			);
			**/
			if( mapping.getIndex() == 0 && !StringUtils.isNullOrEmpty(mapping.getColumn()) 
					&& org.apache.commons.codec.binary.StringUtils.equals( mapping.getColumn(), JdbcUtils.lookupColumnName(rsmd, index) )) {
				indexToUse = index;
			}
			
			if(index == indexToUse ){
				if( String.class == mapping.getJavaType() ){	 
					String value = null ;
					if( mapping.getJdbcType() != null && ( mapping.getJdbcType() == JdbcType.DATE || mapping.getJdbcType() == JdbcType.TIMESTAMP )) {						
						SimpleDateFormat sdf = new SimpleDateFormat( StringUtils.defaultString(mapping.getPattern(), "yyyyMMdd" ));		
						Date date = rs.getDate(index) ;
						value = date != null ? sdf.format(rs.getDate(index)) : null;
						return value;
					}else {
						value = rs.getString(index); 
					} 
					
					if(StringUtils.isEmpty(value))
						value = "";		
					
					// Cipher CASE
					if(!StringUtils.isEmpty( mapping.getCipher())){		
						try {
							Cipher cipher = Cipher.getInstance(mapping.getCipher());
							SecretKeySpec skeySpec = new SecretKeySpec(Hex.decodeHex(mapping.getCipherKey().toCharArray()), mapping.getCipherKeyAlg());
							cipher.init(Cipher.DECRYPT_MODE, skeySpec);
							
							byte raw[] ;
							if(!StringUtils.isEmpty( mapping.getEncoding())){
								String enc = mapping.getEncoding();
								if(enc.toUpperCase().equals("BASE64")){
									//BASE64Decoder decoder = new BASE64Decoder();									
							        //raw = decoder.decodeBuffer(value);	
							        raw = Base64.decodeBase64(value);
								}else if(enc.toUpperCase().equals("HEX")){
									raw = Hex.decodeHex(value.toCharArray());
								}else{
									raw = value.getBytes();
								}
							}else{
								raw= value.getBytes();
							}
					        byte stringBytes[] = cipher.doFinal(raw);
					        return new String(stringBytes);									

						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}								
						return value;
					}
					
					// Encoding CASE
					if(!StringUtils.isEmpty( mapping.getEncoding())){								
						String[] encoding = StringUtils.split(mapping.getEncoding(), ">");								
						try {
							if( encoding.length == 2 )
								return new String(value.getBytes(encoding[0]), encoding[1]);
							else if ( encoding.length == 1 ){
								return new String(value.getBytes(), encoding[0] );
							}
						} catch (UnsupportedEncodingException e) {
							log.error(e.getMessage(), e);
							return value;
						}	
					}
					
				}else if (Long.class == mapping.getJavaType() ){
					String value = rs.getString(index);
					if(StringUtils.isEmpty(value))
						value = "0";							
					return new Long(value);
				}else if (Integer.class == mapping.getJavaType() ){
					String value = rs.getString(index);
					if(StringUtils.isEmpty(value))
						value = "0";							
					return new Integer(value);
				}else if (Double.class == mapping.getJavaType()){
					String value = rs.getString(index);
					if(StringUtils.isEmpty(value))
						value = "0";	
					return new Double(value);
				}
			}
		}		
		return super.getColumnValue(rs, index);
	}

	public CustomColumnMapMapper() {
	}

}
