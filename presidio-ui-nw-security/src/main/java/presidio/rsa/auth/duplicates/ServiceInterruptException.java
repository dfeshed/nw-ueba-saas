package presidio.rsa.auth.duplicates;

/**
 * This class defines an exception that components can throw during initialization of the lifecycle to signal the
 * service cannot continue.
 *
 * @author Tim Menninger
 * @since 0.9
 */
public class ServiceInterruptException extends Exception {


    /**
     * Create a new instance of a configuration exception
     *
     * @param msg The error message
     */
    public ServiceInterruptException(String msg) {

        super(msg);
    }

    /**
     * Create a new instance of a configuration exception
     *
     * @param msg The error message
     * @param cause The contributing exception
     */
    public ServiceInterruptException(String msg, Throwable cause) {

        super(msg, cause);
    }

    /**
     * Create a new instance of a configuration exception
     *
     * @param cause The contributing exception
     */
    public ServiceInterruptException(Throwable cause) {
        super(cause);
    }

}
