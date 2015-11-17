package fortscale.streaming.service;

import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
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
public abstract  class StreamingTaskConfigurationService<T extends  StreamingTaskConfig> {

    protected Map<StreamingTaskDataSourceConfigKey, T> configs = new HashMap<>();
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public StreamingTaskConfigurationService(Map<StreamingTaskDataSourceConfigKey, T> configs) {
        checkNotNull(configs);

        this.configs = configs;
    }
    /**
     * get the output topic according to config key: data source and last state.
     * @param configKey
     * @return
     */
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
