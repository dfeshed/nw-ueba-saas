package fortscale.streaming.service;

public class SamzaStoreWasNotSetException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SamzaStoreWasNotSetException(){
		super("Samza Store was not set.");
	}
}
