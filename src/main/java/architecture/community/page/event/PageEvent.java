package architecture.community.page.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import architecture.community.page.Page;

public class PageEvent extends ApplicationEvent {

	public enum Type {

		CREATED,

		UPDATED,

		DELETED,

		MOVED,

		EXPIRED,

		VIEWED
	};

	private Type type;
	private Date date;

	public PageEvent(Page source, Type type) {
		super(source);
		this.type = type;
		date = new Date();
	}

	/**
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return type
	 */
	public Type getType() {
		return type;
	}

}
