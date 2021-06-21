package architecture.community.content.bundles.dao;

import java.io.InputStream;

import architecture.community.content.bundles.Asset;

public interface BundleDao {
	
	public Asset saveOrUpdate(Asset asset);
	
	public Asset getAssetByAssetId(long assetId);
	
	public void deleteAsset(Asset asset) ;
	
	public void deleteAssetData(Asset asset);
	
	public InputStream getAssetData(Asset asset);
	
	public void saveAssetData(Asset asset, InputStream inputstream);

}
