package fortscale.streaming.exceptions;

/**
 * Created by shays on 25/04/2016.
 */
public class AlertCreationException extends Exception {

    public AlertCreationException() {
    }

    public AlertCreationException(String message) {
        super(message);
    }

    public AlertCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
