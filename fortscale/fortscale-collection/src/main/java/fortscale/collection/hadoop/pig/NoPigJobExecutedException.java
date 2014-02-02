package fortscale.collection.hadoop.pig;

public class NoPigJobExecutedException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoPigJobExecutedException(String message) {
		super(message);
	}

}
