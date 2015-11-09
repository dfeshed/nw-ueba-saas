package fortscale.streaming.task;

import fortscale.streaming.service.state.StreamingTaskMessageState;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

/**
 * Decorator of the basic MessageCollector. Decorates the message with the task's state
 *
 * @author gils
 * Date: 09/11/2015
 */
public class MessageCollectorStateDecorator implements MessageCollector {

    private static Logger logger = LoggerFactory.getLogger(MessageCollectorStateDecorator.class);

    private static final String LAST_STATE_FIELD = "last_state";

    private MessageCollector messageCollector;
    private StreamingTaskMessageState streamingTaskMessageState;

    public MessageCollectorStateDecorator(MessageCollector messageCollector, StreamingTaskMessageState streamingTaskMessageState) {
        this.messageCollector = messageCollector;
        this.streamingTaskMessageState = streamingTaskMessageState;
    }

    @Override
    public void send(OutgoingMessageEnvelope envelope) {
        try {
            // extract original envelope values
            SystemStream systemStream = envelope.getSystemStream();
            Object partitionKey = envelope.getPartitionKey();
            String messageText = (String) envelope.getMessage();

            JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

            message.put(LAST_STATE_FIELD, streamingTaskMessageState.serialize());

            OutgoingMessageEnvelope outgoingMessageEnvelope = new OutgoingMessageEnvelope(systemStream, partitionKey, message.toJSONString());

            messageCollector.send(outgoingMessageEnvelope);
        } catch (Exception e) {
            logger.error("Error while trying to send stateful message:", e);
            // as a fallback send the original envelope without the state field
            messageCollector.send(envelope);
        }
    }
}
