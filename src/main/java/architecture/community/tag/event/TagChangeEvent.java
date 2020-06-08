package architecture.community.tag.event;

import org.springframework.context.ApplicationEvent;

import architecture.ee.component.event.PropertyChangeEvent.Type;

public class TagChangeEvent extends ApplicationEvent  {
	
	
	private Type eventType;
	
	private Object newValue;
	
	private int objectType = 0;
	
	private long objectId = -1L;

	public TagChangeEvent(Object source, Object newValue) {
		super(source); 
		this.newValue = newValue; 
		this.eventType = Type.NONE;
	}

	public TagChangeEvent(Object source, Type eventType, Object newValue) {
		super(source); 
		this.newValue = newValue; 
		this.eventType = eventType;
	}
	
	
	
	public TagChangeEvent(Object source, Type eventType, Object newValue, int objectType, long objectId) {
		super(source);
		this.eventType = eventType;
		this.newValue = newValue;
		this.objectType = objectType;
		this.objectId = objectId;
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

	public Type getEventType() {
		return eventType;
	}

	public void setEventType(Type eventType) {
		this.eventType = eventType;
	}

	public Object getValue() {
		return newValue;
	}

	public void setValue(Object value) {
		this.newValue = value;
	}
	
}
