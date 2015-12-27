package fortscale.services.exceptions;

public class HdfsException extends Exception {
	private static final long serialVersionUID = 1L;

	public HdfsException(String msg, Exception e) {
		super(msg, e);
	}
}
