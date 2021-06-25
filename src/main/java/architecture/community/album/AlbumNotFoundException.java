package architecture.community.album;

import architecture.community.exception.NotFoundException;

public class AlbumNotFoundException extends NotFoundException{

	public AlbumNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public AlbumNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public AlbumNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AlbumNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AlbumNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
