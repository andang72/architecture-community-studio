package architecture.community.user.event;

import org.springframework.context.ApplicationEvent;

import architecture.community.user.User;

public class UserActivityEvent extends ApplicationEvent {

	public enum ACTIVITY {
		SIGNIN, SIGNUP, LOGOUT
	}

	private User user;

	private ACTIVITY activity;

	public UserActivityEvent(Object source, User user, ACTIVITY activity) {
		super(source);
		this.user = user;
		this.activity = activity;
	}

	public User getUser() {
		return user;
	}

	public ACTIVITY getActivity() {
		return activity;
	}

	@Override
	public String toString() {
		return "UserActivityEvent [user=" + user.getUsername() + ", activity=" + activity.name() + "]";
	}

}
