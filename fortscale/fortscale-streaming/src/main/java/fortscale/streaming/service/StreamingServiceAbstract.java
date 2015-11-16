package fortscale.streaming.service;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 09/11/2015.
 * Abstract class for common services of steaming tasks.
 * The <T> is the type of the configuration file and must implement StreamingTaskConfig
 */
public abstract class StreamingServiceAbstract<T extends StreamingTaskConfig> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NO_INPUT_TOPIC_LABEL = "No Input Topic";
    private static final String NULL_EVENT_INPUT_TOPIC_LABEL = "Null event  Input Topic";

    protected Map<StreamingTaskDataSourceConfigKey, T> configs = new HashMap<>();

    public StreamingServiceAbstract(Map<StreamingTaskDataSourceConfigKey, T> configs) {
        this.configs = configs;
    }

    public String getOutputTopic(StreamingTaskDataSourceConfigKey configKey) {
        if (configs.containsKey(configKey))
            return configs.get(configKey).getOutputTopic();
        else {
            throw new IllegalStateException("Could not find any configuration match for data source " + configKey.getDataSource() + " and state " + configKey.getLastState());
        }
    }

    /** Get the partition key to use for outgoing message envelope for the given event */
    public Object getPartitionKey(StreamingTaskDataSourceConfigKey configKey, JSONObject event) {
        if (configs.containsKey(configKey))
            return event.get(configs.get(configKey).getPartitionField());
        else {
            throw new IllegalStateException("Could not find any configuration match for data source " + configKey.getDataSource() + " and state " + configKey.getLastState());
        }
    }
}
