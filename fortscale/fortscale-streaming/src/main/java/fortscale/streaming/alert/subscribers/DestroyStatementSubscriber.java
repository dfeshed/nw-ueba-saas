package fortscale.streaming.alert.subscribers;

import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class DestroyStatementSubscriber extends AbstractSubscriber {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(DestroyStatementSubscriber.class);

    /**
     * Listener method called when Esper has detected a pattern match.
     * Destroy the statement- used for memory usage purposes
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        if (esperStatement != null){
            // sleep to allow creating all relevant statements (before destroying this one)
            try {
                Thread.sleep(10000l);
            }
            catch (Exception e){
            }
            esperStatement.destroy();
        }
    }
}
