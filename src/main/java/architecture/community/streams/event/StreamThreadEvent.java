package architecture.community.streams.event;

import org.springframework.context.ApplicationEvent;

public class StreamThreadEvent extends ApplicationEvent {

	public enum Type {

		CREATED,

		UPDATED,

		DELETED,

		MOVED,

		VIEWED
	};
	
	private Type type ;


	public StreamThreadEvent(Object source, Type type) { 
		super(source); 
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
