package fortscale.streaming.service;

/**
 * Created by shays on 09/11/2015.
 */
public interface StreamingTaskConfig {
    public String getPartitionField();
    public String getOutputTopic();
    public String getInputTopic();
}
