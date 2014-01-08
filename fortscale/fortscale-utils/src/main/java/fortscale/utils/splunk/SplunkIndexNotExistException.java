package fortscale.utils.splunk;

public class SplunkIndexNotExistException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public SplunkIndexNotExistException(String message) {
		super(message);
	}
}
