package architecture.community.share.dao;

import architecture.community.share.SharedLink;

public interface SharedLinkDao {

	public SharedLink getSharedLinkByObjectTypeAndObjectId(Integer objectType, Long objectId) ;
 
	public SharedLink getSharedLink(String linkId) ;
 
	public void saveOrUpdateSharedLink(SharedLink link) ;
 
	public void removeSharedLinkById(String linkId) ;
 
	public void removeSharedLinkByObjectTypeAndObjectId(int objectType, long objectId);
}
