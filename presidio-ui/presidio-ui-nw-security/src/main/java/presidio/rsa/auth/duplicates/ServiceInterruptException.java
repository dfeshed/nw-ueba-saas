package presidio.rsa.auth.duplicates;

/**
 * Copied from Launch project- need to decide how to reuse code without being depended on all launch project
 * TODO: https://bedfordjira.na.rsa.net/browse/ASOC-55722
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
