package fortscale.streaming.exceptions;

public class TaskCoordinatorException extends Exception {
	private static final long serialVersionUID = 1L;

	public TaskCoordinatorException(String msg, Exception e) {
		super(msg, e);
	}
}
