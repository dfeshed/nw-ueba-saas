package fortscale.dataqueries.querygenerators.exceptions;

/**
 * Exception for invalid queries
 */
public class InvalidQueryException extends Exception {

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidQueryException(String message) {
		super(message);
	}
}
