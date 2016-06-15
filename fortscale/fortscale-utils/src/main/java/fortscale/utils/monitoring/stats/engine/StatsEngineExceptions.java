package fortscale.utils.monitoring.stats.engine;

/**
 *
 * A class the holds stats engine exceptions
 *
 * Created by gaashh on 4/17/16.
 */
public class StatsEngineExceptions {

    /**
     * Failed to serialize model engine data to a JSON string
     */
    static public class ModelEngineDataToJsonFailureException extends RuntimeException {

        public ModelEngineDataToJsonFailureException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

}
