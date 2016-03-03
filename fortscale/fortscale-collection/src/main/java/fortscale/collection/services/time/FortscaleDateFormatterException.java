package fortscale.collection.services.time;

/**
 * Representation of fortscale date formatter exception
 *
 * @author gils
 * 03/03/2016
 */
public class FortscaleDateFormatterException extends Exception {
    private static final long serialVersionUID = 1L;

    public FortscaleDateFormatterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FortscaleDateFormatterException(String message)
    {
        super(message);
    }
}
