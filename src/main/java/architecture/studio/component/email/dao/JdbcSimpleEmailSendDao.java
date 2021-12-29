package architecture.studio.component.email.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.studio.component.email.SendBulkEmail;
import architecture.studio.component.email.SendEmail;
import architecture.studio.component.templates.DefaultTemplates;

public class JdbcSimpleEmailSendDao extends ExtendedJdbcDaoSupport implements SimpleEmailSendDao {
 
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	
	private String popertyTableName = "AC_UI_BULK_EMAIL_PROPERTY";
	private String propertyPrimaryColumnName = "BULK_EMAIL_ID";
	
	private boolean propertyEnabled = false;
	
	private final RowMapper<SendBulkEmail> bulkEmailMapper = new RowMapper<SendBulkEmail>(){
		public SendBulkEmail mapRow(ResultSet rs, int rowNum) throws SQLException {
			SendBulkEmail forms = new SendBulkEmail();		
			forms.setEmailId(rs.getLong("BULK_EMAIL_ID")); 
			forms.setTemplates(new DefaultTemplates(rs.getLong("TEMPLATE_ID")));
			forms.setFromEmailAddress(rs.getString("FROM_EMAIL_ADDRESS"));
			forms.setCreator(new UserTemplate(rs.getLong("CREATOR")));
			forms.setModifier(new UserTemplate(rs.getLong("LAST_MODIFIER")));
			forms.setCreationDate(rs.getDate("CREATION_DATE"));
			forms.setModifiedDate(rs.getDate("MODIFIED_DATE"));			
			return forms;
		}		
	};
	
	public long getNextId() {
		return sequencerFactory.getNextValue(Models.SEND_BULK_EMAIL.getObjectType(), Models.SEND_BULK_EMAIL.name());
	}
	
	public Map<String, String> getProperties(long sendEmailId ) {
		return propertyDao.getProperties(popertyTableName, propertyPrimaryColumnName, sendEmailId);
	}

	public void deleteProperties(long formId) {
		propertyDao.deleteProperties(popertyTableName, propertyPrimaryColumnName, formId);
	}

	public void setProperties(long sendEmailId, Map<String, String> props) {
		propertyDao.updateProperties(popertyTableName, propertyPrimaryColumnName, sendEmailId, props);
	}
	
	
	public SendBulkEmail getSendBulkEmail(long sendEmailId) throws NotFoundException {
		SendBulkEmail bulkemail = getExtendedJdbcTemplate().queryForObject( 
				getBoundSql("STUDIO_OUTBOUND.SELECT_BULK_EMAIL_BY_ID").getSql(), 
				bulkEmailMapper, 
				new SqlParameterValue (Types.NUMERIC, sendEmailId ));	 
		if(propertyEnabled)
			bulkemail.setProperties( getProperties(sendEmailId) ); 
		return bulkemail; 
	}
 
	
	public void saveOrUpdate(SendEmail sendEmail) {  
		if( sendEmail.getEmailId() > 0 )
			updateSendEmail(sendEmail);
		else 
			creaetSendEmail(sendEmail);
	}
 
	
	public SendEmail creaetSendEmail(SendEmail sendEmail ) {
		SendBulkEmail toUse = (SendBulkEmail)sendEmail;
		if (toUse.getEmailId() < 1L) {
			long emailId = getNextId();
			toUse.setEmailId(emailId);
		} 
		getExtendedJdbcTemplate().update(getBoundSql("STUDIO_OUTBOUND.INSERT_BULK_EMAIL").getSql(),
				new SqlParameterValue(Types.NUMERIC, toUse.getEmailId()),
				new SqlParameterValue(Types.NUMERIC, toUse.getTemplates().getTemplatesId()),
				new SqlParameterValue(Types.VARCHAR, toUse.getFromEmailAddress()), 
				new SqlParameterValue(Types.INTEGER, toUse.getCreator().getUserId()), 
				new SqlParameterValue(Types.INTEGER, toUse.getModifier().getUserId()),
				new SqlParameterValue(Types.DATE, toUse.getCreationDate()),
				new SqlParameterValue(Types.DATE, toUse.getModifiedDate())); 
		if (propertyEnabled && toUse.getProperties().size() > 0)
			setProperties(toUse.getEmailId(), toUse.getProperties()); 
		return toUse;
	}

	public SendEmail updateSendEmail(SendEmail sendEmail) {
		SendBulkEmail toUse = (SendBulkEmail)sendEmail;
		getExtendedJdbcTemplate().update(getBoundSql("STUDIO_OUTBOUND.UPDATE_BULK_EMAIL").getSql(),  
				new SqlParameterValue(Types.NUMERIC, toUse.getTemplates().getTemplatesId()),
				new SqlParameterValue(Types.VARCHAR, toUse.getFromEmailAddress()), 
				new SqlParameterValue(Types.INTEGER, toUse.getModifier().getUserId()),
				new SqlParameterValue(Types.DATE, toUse.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, toUse.getEmailId()));  
		if(propertyEnabled)
			deleteProperties(toUse.getEmailId());
		if (propertyEnabled && toUse.getProperties().size() > 0)
			setProperties(toUse.getEmailId(), toUse.getProperties()); 
		return toUse;
	}
	
	public void remove( SendEmail sendEmail) throws IOException {  
		getJdbcTemplate().update( getBoundSql("STUDIO_OUTBOUND.DELETE_BULK_EMAIL_BY_ID" ).getSql(), new SqlParameterValue (Types.NUMERIC, sendEmail.getEmailId() ) );
		if(propertyEnabled)
			deleteProperties(sendEmail.getEmailId()); 
	} 

}
