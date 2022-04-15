package architecture.community.streams.event;

import org.springframework.context.ApplicationEvent;

public class StreamMessageEvent extends ApplicationEvent {

    public enum Type {

		CREATED,

		UPDATED,

		DELETED, 

		VIEWED
	};
	
	private Type type ;


	public StreamMessageEvent(Object source, Type type) { 
		super(source); 
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
