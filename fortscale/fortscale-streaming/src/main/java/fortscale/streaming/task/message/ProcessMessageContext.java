package fortscale.streaming.task.message;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;

/**
 * Created by baraks on 12/19/2016.
 */
public interface ProcessMessageContext {
    String getTopicName();
    String getMessageAsString();
    JSONObject getMessageAsJson();
    StreamingTaskDataSourceConfigKey getStreamingTaskDataSourceConfigKey();
}
