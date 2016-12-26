package fortscale.streaming.task.message;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.streaming.task.metrics.StreamingTaskCommonMetrics;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;

import static fortscale.streaming.task.AbstractStreamTask.DATA_SOURCE_FIELD_NAME;
import static fortscale.streaming.task.AbstractStreamTask.LAST_STATE_FIELD_NAME;

/**
 * this class contains the original Samza message, and some useful meta data such as the message as json, its topic etc...
 *
 * Created by baraks on 12/12/2016.
 */
public class StreamingProcessMessageContext implements ProcessMessageContext {
    private final static Logger logger = Logger.getLogger(StreamingProcessMessageContext.class);
    private final AbstractStreamTask streamTask;
    private TaskCoordinator coordinator;
    private MessageCollector collector;
    private String topicName;
    private JSONObject messageAsJson;
    private String messageAsString;
    private IncomingMessageEnvelope incomingMessageEnvelope;
    private StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey;
    private StreamingTaskCommonMetrics streamingTaskCommonMetrics;


    /**
     *
     * @param incomingMessageEnvelope the original message
     * @param collector
     * @param coordinator
     * @param streamTask
     * @throws ParseException in case the message cannot be parsed into jsonObject
     */
    public StreamingProcessMessageContext(IncomingMessageEnvelope incomingMessageEnvelope, MessageCollector collector, TaskCoordinator coordinator, AbstractStreamTask streamTask) throws ParseException {
        this.streamTask = streamTask;
        this.streamingTaskCommonMetrics = streamTask.getStreamingTaskCommonMetrics();
        this.incomingMessageEnvelope = incomingMessageEnvelope;
        this.messageAsString = (String) incomingMessageEnvelope.getMessage();
        this.topicName = getIncomingMessageTopicName(incomingMessageEnvelope);
        this.messageAsJson = parseJsonMessage(messageAsString);
        this.collector = collector;
        this.coordinator = coordinator;
        extractDataSourceConfigKey();
        this.streamTask.countNewMessage(streamingTaskDataSourceConfigKey);
    }


    /**
     * @return message key indicates the partition
     */
    @Override
    public String getKey() {
        return (String) incomingMessageEnvelope.getKey();
    }

    /**
     * @return from which kafka topic did the message arrive
     */
    @Override
    public String getTopicName() {
        return topicName;
    }

    /**
     * @return string representation of the message
     */
    @Override
    public String getMessageAsString() {
        return messageAsString;
    }

    /**
     * @return json representation of the message
     */
    @Override
    public JSONObject getMessageAsJson() {
        return messageAsJson;
    }

    /**
     *
     * @return message data source and last state
     */
    @Override
    public StreamingTaskDataSourceConfigKey getStreamingTaskDataSourceConfigKey() {
        return streamingTaskDataSourceConfigKey;
    }


    /**
     *
     * @return the original Samza message
     */
    public IncomingMessageEnvelope getIncomingMessageEnvelope() {
        return incomingMessageEnvelope;
    }

    /**
     * Get topic name out of incoming message envelope
     *
     * @param envelope - message received in {@link fortscale.streaming.task.AbstractStreamTask#processMessage(ProcessMessageContext)}
     * @return topic name of incoming message
     */
    private String getIncomingMessageTopicName(IncomingMessageEnvelope envelope) {
        return envelope.getSystemStreamPartition().getSystemStream().getStream();
    }

    /**
     * extracts dataSource name from message json
     */
    protected void extractDataSourceConfigKey() {
        if (messageAsJson==null)
        {
            return;
        }
        String dataSource = messageAsJson.getAsString(DATA_SOURCE_FIELD_NAME);
        String lastState = messageAsJson.getAsString(LAST_STATE_FIELD_NAME);

        if (dataSource == null) {

            streamingTaskCommonMetrics.messagesWithoutDataSourceName++;
            logger.warn("Message does not contain " + DATA_SOURCE_FIELD_NAME + " field: " + messageAsString);
            return;
        }
        streamingTaskDataSourceConfigKey = new StreamingTaskDataSourceConfigKey(dataSource, lastState);
    }

    public MessageCollector getCollector() {
        return collector;
    }

    public TaskCoordinator getCoordinator() {
        return coordinator;
    }

    /**
     * @return toString you know...
     */
    @Override
    public String toString()
    {
        return String.format("message=%s topic=%s key=%s",messageAsString,topicName,getKey());
    }

    public JSONObject parseJsonMessage(String msg) throws ParseException {
        JSONObject jsonObject = null;
        try {
            streamingTaskCommonMetrics.parseMessageToJson++;
            jsonObject = (JSONObject) JSONValue.parseWithException(msg);
        } catch (Exception e) {
            streamingTaskCommonMetrics.parseMessageToJsonExceptions++;
        }
        return jsonObject;
    }
}
