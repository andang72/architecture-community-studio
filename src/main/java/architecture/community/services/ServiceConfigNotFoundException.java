package architecture.community.services;

import org.springframework.core.NestedRuntimeException;

public class ServiceConfigNotFoundException extends NestedRuntimeException {

	public ServiceConfigNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ServiceConfigNotFoundException(String msg) {
		super(msg);
	}

}
