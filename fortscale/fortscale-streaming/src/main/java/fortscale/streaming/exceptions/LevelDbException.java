package fortscale.streaming.exceptions;

public class LevelDbException extends Exception{
	private static final long serialVersionUID = 1L;

	public LevelDbException(String msg, Exception e) {
		super(msg, e);
	}
}
