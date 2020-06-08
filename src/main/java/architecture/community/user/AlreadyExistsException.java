package architecture.community.user;

public class AlreadyExistsException extends Exception {

	public AlreadyExistsException() {
		super(); 
	}

	public AlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}

	public AlreadyExistsException(String message, Throwable cause) {
		super(message, cause); 
	}

	public AlreadyExistsException(String message) {
		super(message); 
	}

	public AlreadyExistsException(Throwable cause) {
		super(cause); 
	}

}
