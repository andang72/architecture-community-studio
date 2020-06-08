package architecture.community.i18n.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.i18n.I18nText;
import architecture.community.i18n.dao.I18nTextDao;
import architecture.community.model.Models;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcI18nTextDao extends ExtendedJdbcDaoSupport implements I18nTextDao {

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
	
	private final RowMapper<I18nText> i18nTextMapper = new RowMapper<I18nText>() {		
		public I18nText mapRow(ResultSet rs, int rowNum) throws SQLException {			
			I18nText i18n = new I18nText(rs.getLong("STREAM_ID"));	
			i18n.setLocale(rs.getString("LOCALE"));
			i18n.setKey(rs.getString("TEXT_KEY"));
			i18n.setKey(rs.getString("TEXT"));
			i18n.setObjectType(rs.getInt("OBJECT_TYPE"));
			i18n.setObjectId(rs.getLong("OBJECT_ID"));
			i18n.setCreationDate(rs.getDate("CREATION_DATE"));
			i18n.setModifiedDate(rs.getDate("MODIFIED_DATE"));		
			return i18n;
		}		
	};
	
	public JdbcI18nTextDao() {
		
	}
	
	protected long getNextId(){
		logger.debug("next id for {}, {}", Models.I18N.getObjectType(), Models.I18N.name() );
		return sequencerFactory.getNextValue(Models.I18N.getObjectType(), Models.I18N.name());
	}

	@Override
	public void create(List<I18nText> list) {
		final List<I18nText> textsToUse = list ;
		getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_I18N.INSERT_I18N_OBJECT_TEXT").getSql(),
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						for (I18nText text : textsToUse) {
							long time = System.currentTimeMillis();
							ps.setLong(1, getNextId());
							ps.setInt(2, text.getObjectType());
							ps.setLong(3, text.getObjectId());
							ps.setString(4, text.getLocale());
							ps.setString(5, text.getKey());
							ps.setString(6, text.getText());
							ps.setDate(7, new java.sql.Date(time));
							ps.setDate(8, new java.sql.Date(time));
						}
					}

					public int getBatchSize() {
						return textsToUse.size();
					}
				});
	}

	@Override
	public void update(List<I18nText> list) {
		final List<I18nText> textsToUse = list ;
		getExtendedJdbcTemplate().batchUpdate(getBoundSql("COMMUNITY_I18N.UPDATE_I18N_OBJECT_TEXT").getSql(),
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						for (I18nText text : textsToUse) {
							long time = System.currentTimeMillis();
							ps.setString(1, text.getText());
							ps.setDate(2, new java.sql.Date(time));
							ps.setLong(3, text.getTextId());
						}
					}

					public int getBatchSize() {
						return textsToUse.size();
					}
				});
	}

	@Override
	public List<I18nText> getTexts() {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_I18N.SELECT_ALL_I18N_OBJECT_TEXT").getSql(), 
				i18nTextMapper
				);
	}

	@Override
	public List<I18nText> getTexts(Locale locale) {
		return getExtendedJdbcTemplate().query(
				getBoundSql("COMMUNITY_I18N.SELECT_I18N_OBJECT_TEXT_BY_LOCALE").getSql(), 
				i18nTextMapper, 
				new SqlParameterValue(Types.VARCHAR, locale.toString())
				);
	}
 
	public I18nText getText(long textId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_I18N.SELECT_I18N_OBJECT_TEXT_BY_ID").getSql(), 
				i18nTextMapper,
				new SqlParameterValue(Types.NUMERIC, textId));
	}

	@Override
	public I18nText findByKeyAndLocale(String key, String locale) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_I18N.SELECT_I18N_OBJECT_TEXT_BY_KEY_LOCALE").getSql(), 
				i18nTextMapper,
				new SqlParameterValue(Types.VARCHAR, key),
				new SqlParameterValue(Types.VARCHAR, locale)
				);
	}
 
	public void delete(List<I18nText> list) {
		final List<I18nText> textsToUse = list ;
		getExtendedJdbcTemplate().batchUpdate(
				getBoundSql("COMMUNITY_I18N.DELETE_I18N_OBJECT_TEXT").getSql(),
			    	new BatchPreparedStatementSetter(){
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							for( I18nText text : textsToUse ){
								ps.setLong(1, text.getTextId());
							}
						}
						public int getBatchSize() {
							return textsToUse.size();
					}});
	}

}
