package fortscale.streaming.service;

import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import fortscale.streaming.service.tagging.computer.ComputerTaggingConfig;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by shays on 09/11/2015.
 * Abstract class for common services of steaming tasks.
 * The <T> is the type of the configuration file and must implement StreamingTaskConfig
 */
public abstract  class StreamingServiceAbstract<T extends  StreamingTaskConfig> {

    protected Map<String, T> configs = new HashMap<>();
    public Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String NO_INPUT_TOPIC_LABEL = "No Input Topic";
    public static final String NULL_EVENT_INPUT_TOPIC_LABEL = "Null event  Input Topic";


    /**
     *
     * @param inputTopic - the name of the topic
     * @return
     */
    public String getOutputTopic(String inputTopic) {
        if (configs.containsKey(inputTopic))
            return configs.get(inputTopic).getOutputTopic();
        else
            throw new RuntimeException("received events from topic " + inputTopic + " that does not appear in configuration");
    }

    /** Get the partition key to use for outgoing message envelope for the given event */
    public Object getPartitionKey(String inputTopic, JSONObject event) throws FilteredEventException {

        // get the configuration for the input topic, if not found skip this event
        T config = verifyInputTopicAndEventFetchConfig(inputTopic, event, configs);
        if (config==null) {
            logger.error("received event from topic {} that does not appear in configuration", inputTopic);
            return null;
        }

        return event.get(config.getPartitionField());
    }


    /**
     * Check that configuration is fine and throw FilteredEventException if it doesn't
     * @param inputTopic - the name of the topic
     * @param event - event JSON object
     * @param configs - the map of all configurations per inputTopic
     * @return
     * @throws FilteredEventException
     */
    protected T  verifyInputTopicAndEventFetchConfig(String inputTopic, JSONObject event, Map<String,T> configs)
            throws FilteredEventException {
        try {
            checkNotNull(inputTopic);
        } catch (Exception e){
            throw new FilteredEventException(NO_INPUT_TOPIC_LABEL,e);
        }

        try {
            checkNotNull(event);
        } catch (Exception e){
            throw new FilteredEventException(NULL_EVENT_INPUT_TOPIC_LABEL,e);
        }

        try {
            T config = configs.get(inputTopic);

            if (config==null) {
                logger.error("received event from topic {} that does not appear in configuration", inputTopic);
                throw new FilteredEventException(NULL_EVENT_INPUT_TOPIC_LABEL);
            }
            return config;
        } catch (Exception e){
            throw new FilteredEventException(NULL_EVENT_INPUT_TOPIC_LABEL,e);
        }
    }
}
