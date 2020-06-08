package architecture.community.model;

import java.io.Serializable;

public class ModelObjectKey implements Serializable {

	private int objectType ;
	
	private long objectId ;
	
	public ModelObjectKey() {
		objectType = ModelObject.UNKNOWN_OBJECT_TYPE;
		objectId = ModelObject.UNKNOWN_OBJECT_ID ;
	}

	public ModelObjectKey(int objectType, long objectId) {
		this.objectType = objectType;
		this.objectId = objectId;
	}

}
