package fortscale.common.exceptions;

public class InvalidValueException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public InvalidValueException(String s) {
        super(s);
    }
}
