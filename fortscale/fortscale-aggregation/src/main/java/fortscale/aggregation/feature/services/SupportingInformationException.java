package fortscale.aggregation.feature.services;

/**
 * Created by gils on 28/07/2015.
 */
public class SupportingInformationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SupportingInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SupportingInformationException(String message)
    {
        super(message);
    }
}
