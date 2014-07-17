package fortscale.streaming.exceptions;

public class KafkaPublisherException extends Exception {
	private static final long serialVersionUID = 1L;

	public KafkaPublisherException(String msg, Exception e) {
		super(msg, e);
	}
}
