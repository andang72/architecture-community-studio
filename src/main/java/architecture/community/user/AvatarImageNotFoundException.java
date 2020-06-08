package architecture.community.user;

import architecture.community.exception.NotFoundException;

public class AvatarImageNotFoundException extends NotFoundException {

	public AvatarImageNotFoundException() {
		super(); 
	}

	public AvatarImageNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}

	public AvatarImageNotFoundException(String message, Throwable cause) {
		super(message, cause); 
	}

	public AvatarImageNotFoundException(String message) {
		super(message); 
	}

	public AvatarImageNotFoundException(Throwable cause) {
		super(cause); 
	}

}
