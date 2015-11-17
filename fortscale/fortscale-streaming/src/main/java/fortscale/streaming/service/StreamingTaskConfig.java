package fortscale.streaming.service;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;

/**
 * Created by shays on 09/11/2015.
 * Interface for configration of steaming task.
 * Define common methods for all configurations
 */
public interface StreamingTaskConfig {
    public String getPartitionField();
    public String getOutputTopic();
    public StreamingTaskDataSourceConfigKey getConfigKey();
}
