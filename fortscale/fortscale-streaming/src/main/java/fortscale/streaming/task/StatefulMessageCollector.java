package fortscale.streaming.task;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

/**
 * @author gils
 * Date: 09/11/2015
 */
public class StatefulMessageCollector implements MessageCollector {

    private static Logger logger = LoggerFactory.getLogger(StatefulMessageCollector.class);

    private static final String LAST_STATE_FIELD = "last_state";
    private MessageCollector messageCollector;

    public StatefulMessageCollector(MessageCollector messageCollector) {
        this.messageCollector = messageCollector;
    }

    @Override
    public void send(OutgoingMessageEnvelope envelope) {

        try {
            // extract original envelope values
            SystemStream systemStream = envelope.getSystemStream();
            Object partitionKey = envelope.getPartitionKey();
            String messageText = (String) envelope.getMessage();

            JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

            // add last state field to message
            message.put(LAST_STATE_FIELD, "xxxxx");

            OutgoingMessageEnvelope outgoingMessageEnvelope = new OutgoingMessageEnvelope(systemStream, partitionKey, message);

            messageCollector.send(outgoingMessageEnvelope);
        } catch (Exception e) {
            logger.error("Error while trying to send stateful message:", e);
            // as a fallback send the original envelope without the state field
            messageCollector.send(envelope);
        }
    }
}
