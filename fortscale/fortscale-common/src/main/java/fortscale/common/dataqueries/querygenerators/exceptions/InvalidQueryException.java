package fortscale.common.dataqueries.querygenerators.exceptions;

/**
 * Exception for invalid queries
 */
public class InvalidQueryException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidQueryException(String message) {
		super(message);
	}
}
