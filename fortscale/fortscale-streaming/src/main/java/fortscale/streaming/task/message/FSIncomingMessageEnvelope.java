package fortscale.streaming.task.message;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;

/**
 * Created by baraks on 12/12/2016.
 */
public class FSIncomingMessageEnvelope extends IncomingMessageEnvelope {
    private JSONObject jsonMsg;
    private String stringMessage;
    private StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey;
    /**
     * Constructs a new IncomingMessageEnvelope from specified components.
     *
     * @param systemStreamPartition The aggregate object representing the incoming stream name, the name of the cluster
     *                              from which the stream came, and the partition of the stream from which the message was received.
     * @param offset                The offset in the partition that the message was received from.
     * @param key                   A deserialized key received from the partition offset.
     * @param message               A deserialized message received from the partition offset.
     */
    public FSIncomingMessageEnvelope(SystemStreamPartition systemStreamPartition, String offset, Object key, Object message) throws ParseException {
        super(systemStreamPartition, offset, key, message);
        stringMessage = (String) message;
    }

    public void setJsonMsg(JSONObject jsonMsg) {
        this.jsonMsg = jsonMsg;
    }

    public JSONObject getJsonMsg() {
        return jsonMsg;
    }

    public String getStringMessage() {
        return stringMessage;
    }

    public StreamingTaskDataSourceConfigKey getStreamingTaskDataSourceConfigKey() {
        return streamingTaskDataSourceConfigKey;
    }

    public void setStreamingTaskDataSourceConfigKey(StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey) {
        this.streamingTaskDataSourceConfigKey = streamingTaskDataSourceConfigKey;
    }
}
