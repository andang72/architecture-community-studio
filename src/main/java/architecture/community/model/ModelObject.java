package architecture.community.model;

public interface ModelObject {

	public static final int UNKNOWN_OBJECT_TYPE = Models.UNKNOWN.getObjectType() ;
	
	public static final long UNKNOWN_OBJECT_ID = -1L ;
	
	public abstract int getObjectType();
	
	public abstract long getObjectId();
	
}
