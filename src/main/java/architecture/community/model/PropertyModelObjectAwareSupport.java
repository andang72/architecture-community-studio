package architecture.community.model;

public class PropertyModelObjectAwareSupport extends PropertyAwareSupport implements PropertyModelObjectAware {

	int objectType = UNKNOWN_OBJECT_TYPE;
	long objectId = UNKNOWN_OBJECT_ID ;
	
	public PropertyModelObjectAwareSupport(int objectType, long objectId) {
		this.objectType = objectType;
		this.objectId = objectId;
	}
	public int getObjectType() {
		return objectType;
	}
	
	public long getObjectId() {
		return objectId;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	
}
