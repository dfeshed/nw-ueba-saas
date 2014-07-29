package fortscale.streaming.exceptions;

public class StreamMessageNotContainFieldException extends Exception {
	private static final long serialVersionUID = 1L;

	public StreamMessageNotContainFieldException(String messageText, String fieldName){
		super(String.format("message %s does not contains field %s", messageText, fieldName));
	}
}
