package fortscale.common.exceptions;

public class UserAlreadyExistException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
     * Constructs an <code>AlreadyExistsException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public UserAlreadyExistException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>AlreadyExistsException</code> with the specified message
     * and root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public UserAlreadyExistException(String msg, Throwable t) {
        super(msg, t);
    }
}
