package fortscale.streaming.service;

public class PrevalanceModelBuilderWasNotSetException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PrevalanceModelBuilderWasNotSetException(){
		super("PrevalanceModelBuilder was not set.");
	}
}
