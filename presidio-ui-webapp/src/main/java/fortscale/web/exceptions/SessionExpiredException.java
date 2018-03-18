package fortscale.web.exceptions;

import org.springframework.security.core.AuthenticationException;

public class SessionExpiredException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public SessionExpiredException(String msg, Throwable t) {
		super(msg, t);
	}

}
