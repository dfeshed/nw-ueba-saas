package fortscale.streaming.exceptions;

import fortscale.streaming.task.message.ProcessMessageContext;

public class StreamMessageNotContainFieldException extends Exception {
	private static final long serialVersionUID = 1L;

	public StreamMessageNotContainFieldException(String messageText, String fieldName){
		super(String.format("message %s does not contains field %s", messageText, fieldName));
	}
	public StreamMessageNotContainFieldException(ProcessMessageContext messageContext,String fieldName){
		super(String.format("message %s does not contains field %s", messageContext.toString(), fieldName));
	}
}
