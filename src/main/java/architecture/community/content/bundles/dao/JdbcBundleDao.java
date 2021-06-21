package architecture.community.content.bundles.dao;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Normalizer;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;

import architecture.community.attachment.Attachment;
import architecture.community.content.bundles.Asset;
import architecture.community.content.bundles.DefaultAsset;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.spring.jdbc.InputStreamRowMapper;

public class JdbcBundleDao extends ExtendedJdbcDaoSupport  implements BundleDao {

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	
	private String assetsPopertyTableName = "AC_UI_ASSETS_PROPERTY";
	private String assetsPropertyPrimaryColumnName = "ASSET_ID";

	private final RowMapper<Asset> assetMapper = new RowMapper<Asset>(){
		public Asset mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultAsset asset = new DefaultAsset();			
			asset.setAssetId(rs.getLong("ASSET_ID"));
			asset.setObjectType(rs.getInt("OBJECT_TYPE"));
			asset.setObjectId(rs.getLong("OBJECT_ID"));
			asset.setLinkId(rs.getString("LINK_ID"));
			String normalized = java.text.Normalizer.normalize( rs.getString("FILE_NAME"), Normalizer.Form.NFC );
			asset.setFilename(normalized); 
			asset.setFilesize(rs.getInt("FILE_SIZE"));
			asset.setDescription(rs.getString("DESCRIPTION"));
			asset.setEnabled((rs.getInt("ENABLED") == 1 ? true : false));
			asset.setSecured((rs.getInt("SECURED") == 1 ? true : false));
			asset.setCreator(new UserTemplate(rs.getLong("USER_ID")));
			asset.setCreationDate(rs.getDate("CREATION_DATE"));
			asset.setModifiedDate(rs.getDate("MODIFIED_DATE"));			
			return asset;
		}		
	};
	
	public long getNextAssetId() {
		return sequencerFactory.getNextValue(Models.ASSET.getObjectType(), Models.ASSET.name());
	}
	
	public Map<String, String> getAssetProperties(long assetId) {
		return propertyDao.getProperties(assetsPopertyTableName, assetsPropertyPrimaryColumnName, assetId);
	}

	public void deleteAssetProperties(long assetId) {
		propertyDao.deleteProperties(assetsPopertyTableName, assetsPropertyPrimaryColumnName, assetId);
	}

	public void setAssetProperties(long assetId, Map<String, String> props) {
		propertyDao.updateProperties(assetsPopertyTableName, assetsPropertyPrimaryColumnName, assetId, props);
	}
	
	
	public Asset saveOrUpdate(Asset asset) { 
		if (asset.getAssetId() < 1L) {
			return creaetAsset(asset);
		}else {
			return updateAsset(asset);
		} 
	}
	
	public Asset getAssetByAssetId(long assetId) { 
		Asset asset = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_RESOURCES.SELECT_ASSET_BY_ID").getSql(), assetMapper, new SqlParameterValue (Types.NUMERIC, assetId ));	 
		asset.setProperties( getAssetProperties(assetId) );
		return asset;
	}

	
	public Asset creaetAsset(Asset asset) {
		Asset toUse = asset;
		if (toUse.getAssetId() < 1L) {
			long assetId = getNextAssetId();
			if (toUse instanceof DefaultAsset) {
				((DefaultAsset) toUse).setAssetId(assetId);
			}
		}
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_RESOURCES.INSERT_ASSET").getSql(),
				new SqlParameterValue(Types.NUMERIC, asset.getAssetId()),
				new SqlParameterValue(Types.INTEGER, asset.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, asset.getObjectId()),
				new SqlParameterValue(Types.VARCHAR, asset.getLinkId()),
				new SqlParameterValue(Types.VARCHAR, asset.getFilename()),
				new SqlParameterValue(Types.INTEGER, asset.getFilesize()),
				new SqlParameterValue(Types.VARCHAR, asset.getDescription()),
				new SqlParameterValue(Types.INTEGER, asset.isEnabled()?1:0),
				new SqlParameterValue(Types.INTEGER, asset.isSecured()?1:0),
				new SqlParameterValue(Types.NUMERIC, asset.getCreator().getUserId()),
				new SqlParameterValue(Types.DATE, asset.getCreationDate()),
				new SqlParameterValue(Types.DATE, asset.getModifiedDate())); 
		if (asset.getProperties().size() > 0)
			setAssetProperties(toUse.getAssetId(), asset.getProperties()); 
		return toUse;
	}

	public Asset updateAsset(Asset asset) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_RESOURCES.UPDATE_ASSET").getSql(),
				/// new SqlParameterValue (Types.NUMERIC, image.getImageId()),
				new SqlParameterValue(Types.INTEGER, asset.getObjectType()),
				new SqlParameterValue(Types.INTEGER, asset.getObjectId()),
				new SqlParameterValue(Types.VARCHAR, asset.getFilename()),
				new SqlParameterValue(Types.INTEGER, asset.getFilesize()),
				new SqlParameterValue(Types.VARCHAR, asset.getDescription()),
				new SqlParameterValue(Types.INTEGER, asset.isEnabled()?1:0),
				new SqlParameterValue(Types.INTEGER, asset.isSecured()?1:0),
				new SqlParameterValue(Types.DATE, asset.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, asset.getAssetId())); 
		deleteAssetProperties(asset.getAssetId());
		if (asset.getProperties().size() > 0)
			setAssetProperties(asset.getAssetId(), asset.getProperties()); 
		return asset;
	}

	public void deleteAsset(Asset asset) {		
		getJdbcTemplate().update( getBoundSql("COMMUNITY_RESOURCES.DELETE_ASSET_BY_ID" ).getSql(), 
			new SqlParameterValue (Types.NUMERIC, asset.getAssetId()) );
		deleteAssetProperties(asset.getAssetId());
	}
	
	public void deleteAssetData(Asset asset) {		
		getJdbcTemplate().update(
			getBoundSql("COMMUNITY_RESOURCES.DELETE_ASSET_DATA_BY_ID" ).getSql(),
			new SqlParameterValue (Types.NUMERIC, asset.getAssetId())
		);
	}
	
	public InputStream getAssetData(Asset asset) {		
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_RESOURCES.SELECT_ASSET_DATA_BY_ID").getSql(), new InputStreamRowMapper(), new SqlParameterValue (Types.NUMERIC, asset.getAssetId()));		
	}

	public void saveAssetData(Asset asset, InputStream inputstream) {

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_RESOURCES.DELETE_ASSET_DATA_BY_ID").getSql(), new SqlParameterValue (Types.NUMERIC, asset.getAssetId()));		
		if( getExtendedJdbcTemplate().getDBInfo() == DB.ORACLE ){									
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_RESOURCES.CREATE_EMPTY_ASSET_DATA").getSql(), new SqlParameterValue (Types.NUMERIC, asset.getAssetId()));
			getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_RESOURCES.UPDATE_ASSET_DATA").getSql(), new Object[]{ new SqlLobValue( inputstream , asset.getFilesize(), getLobHandler()), asset.getAssetId() }, 
				new int[]{ Types.BLOB, Types.NUMERIC });
		}else{			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_RESOURCES.CREATE_ASSET_DATA").getSql(), 
				new SqlParameterValue ( Types.NUMERIC, asset.getAssetId()), 
				new SqlParameterValue ( Types.BLOB,  new SqlLobValue( inputstream , asset.getFilesize(), getLobHandler() ) ) 
			);
		}
	}

	
}
