package architecture.community.user.event;

import org.springframework.context.ApplicationEvent;

import architecture.community.user.User;

public class UserRemovedEvent extends ApplicationEvent {

	private User user;

	public UserRemovedEvent(Object source, User user) {
		super(source);
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	public String toString() {
		return "UserRemovedEvent [user=" + user + "]";
	}

	
}
