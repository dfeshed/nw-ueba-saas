package fortscale.streaming.alert.subscribers;

import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class SingleTriggerAlertCreationSubscriber extends AlertCreationSubscriber {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(SingleTriggerAlertCreationSubscriber.class);


    /**
     * Listener method called when Esper has detected a pattern match.
     * Creates an alert and saves it in mongo. this includes the references to its evidences, which are already in mongo.
     */
    @Override
    public void update(Map[] eventStreamArr, Map[] removeStream) {
        esperStatement.stop();
        super.update(eventStreamArr,removeStream);
        esperStatement.destroy();
    }
}
