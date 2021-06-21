package architecture.community.web.spring.controller.data.model;

import java.io.Serializable;
import java.util.List;

public class Bag implements Serializable {

	private int objectType ;
	private long fromObjectId ;
	private long toObjectId ;
	
	private List<Long> items ;
	
	public int getObjectType() {
		return objectType;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public long getFromObjectId() {
		return fromObjectId;
	}
	public void setFromObjectId(long fromObjectId) {
		this.fromObjectId = fromObjectId;
	}
	public long getToObjectId() {
		return toObjectId;
	}
	public void setToObjectId(long toObjectId) {
		this.toObjectId = toObjectId;
	}
	public List<Long> getItems() {
		return items;
	}
	public void setItems(List<Long> items) {
		this.items = items;
	}	
		
}
