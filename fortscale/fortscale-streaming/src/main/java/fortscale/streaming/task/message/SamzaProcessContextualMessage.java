package fortscale.streaming.task.message;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;

import java.util.concurrent.atomic.AtomicLong;

import static fortscale.streaming.task.AbstractStreamTask.DATA_SOURCE_FIELD_NAME;
import static fortscale.streaming.task.AbstractStreamTask.LAST_STATE_FIELD_NAME;
import static fortscale.streaming.task.message.FSProcessContextualMessageUtil.parseJsonMessage;

/**
 * this class contains the original Samza message, and some useful meta data such as the message as json, its topic etc...
 *
 * Created by baraks on 12/12/2016.
 */
public class SamzaProcessContextualMessage implements FSProcessContextualMessage {
    private static Logger logger = Logger.getLogger(SamzaProcessContextualMessage.class);

    private String topicName;
    private JSONObject messageAsJson;
    private String messageAsString;
    private IncomingMessageEnvelope incomingMessageEnvelope;
    private StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey;
    private AtomicLong messagesWithoutDataSourceNameCounter;


    /**
     *
     * @param incomingMessageEnvelope the original message
     * @param messageShouldContainDataSourceField true if message-json should contain a dataSource field
     * @param parseToJsonCounter by reference counter that will be updated(+1) when parsing message to json
     * @param parseToJsonExceptionCounter by reference counter that will be updated(+1) when parsing message to json fails
     * @param messagesWithoutDataSourceNameCounter by reference counter that will be updated(+1) get datasource from message fails
     * @throws ParseException in case the message cannot be parsed into jsonObject
     */
    public SamzaProcessContextualMessage(IncomingMessageEnvelope incomingMessageEnvelope, boolean messageShouldContainDataSourceField, AtomicLong parseToJsonCounter, AtomicLong parseToJsonExceptionCounter, AtomicLong messagesWithoutDataSourceNameCounter) throws ParseException {
        this.incomingMessageEnvelope = incomingMessageEnvelope;
        this.messageAsString = (String) incomingMessageEnvelope.getMessage();
        this.topicName = getIncomingMessageTopicName(incomingMessageEnvelope);
        this.messageAsJson = parseJsonMessage(messageAsString, parseToJsonCounter, parseToJsonExceptionCounter);
        this.messagesWithoutDataSourceNameCounter = messagesWithoutDataSourceNameCounter;
        if(messageShouldContainDataSourceField) {
            extractDataSourceConfigKey();
        }
    }

    /**
     * syntactic sugar, no counters will be updated
     * @param incomingMessageEnvelope
     * @throws ParseException
     */
    public SamzaProcessContextualMessage(IncomingMessageEnvelope incomingMessageEnvelope,boolean messageShouldContainDataSourceField) throws ParseException {
        this(incomingMessageEnvelope, messageShouldContainDataSourceField, null,null,null);
    }

    /**
     *
     * @return from which kafka topic did the message arrive
     */
    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public String getMessageAsString() {
        return messageAsString;
    }

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
     * @param envelope - message received in {@link fortscale.streaming.task.AbstractStreamTask#wrappedProcess(FSProcessContextualMessage, MessageCollector, TaskCoordinator)}
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
            if(messagesWithoutDataSourceNameCounter !=null) {
                messagesWithoutDataSourceNameCounter.getAndIncrement();
            }
            logger.warn("Message does not contain " + DATA_SOURCE_FIELD_NAME + " field: " + messageAsString);
            return;
        }
        streamingTaskDataSourceConfigKey = new StreamingTaskDataSourceConfigKey(dataSource, lastState);
    }
}
