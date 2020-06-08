package architecture.community.page;

public class PathPattern { 
	
	private final long objectId; 
	private final String pattern;

	public PathPattern(long objectId, String pattern) {
		super();
		this.objectId = objectId; 
		this.pattern = pattern ;
	}

 	public long getObjectId() {
		return objectId;
	}

	public String getPattern() {
		return pattern;
	}

}
