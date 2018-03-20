package presidio.output.forwarder.strategy;


/**
 * Interface to implement the forwarding strategy
 */
public interface ForwarderStrategy {

    enum TYPE {
        USER, ALERT, INDICATOR;
    }

    /**
     * This is to inform the forwarder to perform initialization
     */
    void init();


    /**
     * Forward the message to the concrete target
     *
     * @param id the id of the object
     * @param message json representation of the object
     * @param type object type
     *
     * @throws Exception if forwarding the message fail
     */
    void forward (String id, String message, TYPE type) throws Exception;


    /**
     * Indicates that the application has finished using the forwarder, and that resources it requires may be released or made available
     */
    void close();

}
