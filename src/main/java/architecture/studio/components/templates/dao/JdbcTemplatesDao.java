package architecture.studio.components.templates.dao;

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
import architecture.studio.components.templates.DefaultTemplates;
import architecture.studio.components.templates.Templates;

public class JdbcTemplatesDao extends ExtendedJdbcDaoSupport implements TemplatesDao {

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	
	private String templatesPopertyTableName = "AC_UI_TEMPLATES_PROPERTY";
	private String templatesPropertyPrimaryColumnName = "TEMPLATE_ID";
	
	private boolean propertyEnabled = false;
	
	private final RowMapper<Templates> templatesMapper = new RowMapper<Templates>(){
		public Templates mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultTemplates forms = new DefaultTemplates();			
			forms.setTemplatesId(rs.getLong("TEMPLATE_ID"));
			forms.setObjectType(rs.getInt("OBJECT_TYPE"));
			forms.setObjectId(rs.getLong("OBJECT_ID")); 
			forms.setName(rs.getString("NAME"));
			forms.setDisplayName(rs.getString("DISPLAY_NAME"));
			forms.setDescription(rs.getString("DESCRIPTION"));
			forms.setSubject(rs.getString("SUBJECT"));
			forms.setBody(rs.getString("BODY"));
			forms.setCreator(new UserTemplate(rs.getLong("CREATOR")));
			forms.setModifier(new UserTemplate(rs.getLong("LAST_MODIFIER")));
			forms.setCreationDate(rs.getDate("CREATION_DATE"));
			forms.setModifiedDate(rs.getDate("MODIFIED_DATE"));			
			return forms;
		}		
	};
	
	public long getNextTemplatesId() {
		return sequencerFactory.getNextValue(Models.TEMPLATES.getObjectType(), Models.TEMPLATES.name());
	}
	
	public Map<String, String> getTemplatesProperties(long formId) {
		return propertyDao.getProperties(templatesPopertyTableName, templatesPropertyPrimaryColumnName, formId);
	}

	public void deleteTemplatestProperties(long formId) {
		propertyDao.deleteProperties(templatesPopertyTableName, templatesPropertyPrimaryColumnName, formId);
	}

	public void setTemplatesProperties(long formId, Map<String, String> props) {
		propertyDao.updateProperties(templatesPopertyTableName, templatesPropertyPrimaryColumnName, formId, props);
	}

	 
	public Long getTemplatesIdByName(String name) {
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("STUDIO_CONTENT.SELECT_TEMPLATES_ID_BY_NAME").getSql(), Long.class, new SqlParameterValue(Types.VARCHAR, name));
	}
 
	public Templates getTemplates(long templateId) throws NotFoundException {
		Templates forms = getExtendedJdbcTemplate().queryForObject( getBoundSql("STUDIO_CONTENT.SELECT_TEMPLATES_BY_ID").getSql(), templatesMapper, new SqlParameterValue (Types.NUMERIC, templateId ));	 
		if(propertyEnabled)
			forms.setProperties( getTemplatesProperties(templateId) );
		return forms;
	} 
 
	public void saveOrUpdate(Templates templates) {
		if( templates.getTemplatesId() > 0 )
			updateAsset(templates);
		else 
			creaetAsset(templates);
	}
	
	public Templates creaetAsset(Templates templates) {
		Templates toUse = templates;
		if (toUse.getTemplatesId() < 1L) {
			long formsId = getNextTemplatesId();
			if (toUse instanceof DefaultTemplates) {
				((DefaultTemplates) toUse).setTemplatesId(formsId);
			}
		}
		
		getExtendedJdbcTemplate().update(getBoundSql("STUDIO_CONTENT.INSERT_TEMPLATES").getSql(),
				new SqlParameterValue(Types.NUMERIC, templates.getTemplatesId()),
				new SqlParameterValue(Types.INTEGER, templates.getObjectType()),
				new SqlParameterValue(Types.INTEGER, templates.getObjectId()),
				new SqlParameterValue(Types.VARCHAR, templates.getName()), 
				new SqlParameterValue(Types.VARCHAR, templates.getDisplayName()), 
				new SqlParameterValue(Types.VARCHAR, templates.getDescription()), 
				new SqlParameterValue(Types.VARCHAR, templates.getSubject()), 
				new SqlParameterValue(Types.VARCHAR, templates.getBody()), 
				new SqlParameterValue(Types.INTEGER, templates.getCreator().getUserId()), 
				new SqlParameterValue(Types.INTEGER, templates.getModifier().getUserId()),
				new SqlParameterValue(Types.DATE, templates.getCreationDate()),
				new SqlParameterValue(Types.DATE, templates.getModifiedDate())); 
		if (propertyEnabled && templates.getProperties().size() > 0)
			setTemplatesProperties(toUse.getTemplatesId(), templates.getProperties()); 
		return toUse;
	}

	public Templates updateAsset(Templates forms) {
		getExtendedJdbcTemplate().update(getBoundSql("STUDIO_CONTENT.UPDATE_TEMPLATES").getSql(), 
				new SqlParameterValue(Types.INTEGER, forms.getObjectType()),
				new SqlParameterValue(Types.INTEGER, forms.getObjectId()),
				new SqlParameterValue(Types.VARCHAR, forms.getName()), 
				new SqlParameterValue(Types.VARCHAR, forms.getDisplayName()), 
				new SqlParameterValue(Types.VARCHAR, forms.getDescription()), 
				new SqlParameterValue(Types.VARCHAR, forms.getSubject()), 
				new SqlParameterValue(Types.VARCHAR, forms.getBody()), 
				new SqlParameterValue(Types.INTEGER, forms.getModifier().getUserId()),
				new SqlParameterValue(Types.DATE, forms.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, forms.getTemplatesId()));  
		if(propertyEnabled)
			deleteTemplatestProperties(forms.getTemplatesId());
		if (propertyEnabled && forms.getProperties().size() > 0)
			setTemplatesProperties(forms.getTemplatesId(), forms.getProperties()); 
		return forms;
	}
	
 
	public void remove(Templates forms) throws IOException {
		getJdbcTemplate().update( getBoundSql("STUDIO_CONTENT.DELETE_TEMPLATES_BY_ID" ).getSql(), new SqlParameterValue (Types.NUMERIC, forms.getTemplatesId() ) );
		if(propertyEnabled)
			deleteTemplatestProperties(forms.getTemplatesId()); 
	}
	
}
