package architecture.community.user;

public class RoleNotFoundException extends Exception {

	public RoleNotFoundException() {
		super(); 
	}

	public RoleNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}

	public RoleNotFoundException(String message, Throwable cause) {
		super(message, cause); 
	}

	public RoleNotFoundException(String message) {
		super(message); 
	}

	public RoleNotFoundException(Throwable cause) {
		super(cause); 
	}

}
