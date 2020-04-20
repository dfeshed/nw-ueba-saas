package presidio.output.forwarder.strategy;


import presidio.output.forwarder.ForwardMassage;

import java.util.List;
import java.util.Map;

/**
 * Interface to implement the forwarding strategy
 */
public interface ForwarderStrategy {

    enum PAYLOAD_TYPE {
        ENTITY, ALERT, INDICATOR;
    }

    String getName();

    /**
     * This is to inform the forwarder to perform initialization
     */
    void init();


    /**
     * Forward the payload to the concrete target
     *
     * @param messages list of messages: <id, message> the id of the object, json representation of the object
     * @param type object type
     *
     * @throws Exception if forwarding the payload fail
     */
    void forward (List<ForwardMassage> messages, PAYLOAD_TYPE type) throws Exception;


    /**
     * Indicates that the application has finished using the forwarder, and that resources it requires may be released or made available
     */
    void close();

}
