package fortscale.streaming.exceptions;

public class KeyValueDBException extends Exception{
	private static final long serialVersionUID = 1L;

	public KeyValueDBException(String msg, Exception e) {
		super(msg, e);
	}
}
