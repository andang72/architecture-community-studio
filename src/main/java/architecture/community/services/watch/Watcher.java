package architecture.community.services.watch;

public class Watcher {
	
	int objectType;
	
	long objectId;
	
	long userId;
	
	int watchType;
	
	public Watcher() {
		
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getWatchType() {
		return watchType;
	}

	public void setWatchType(int watchType) {
		this.watchType = watchType;
	}

}
