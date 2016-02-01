package fortscale.streaming.service.state;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;

/**
 * Decorator to add the 'session' notion to session events in the data-source field
 *
 * @author gils
 * Date: 19/11/2015
 */
public class MessageCollectorSessionDecorator implements MessageCollector{

    private static final String DATA_SOURCE_FIELD = "data_source";
    private static final String SESSION_SUFFIX = "_session";

    private MessageCollector messageCollector;

    public MessageCollectorSessionDecorator(MessageCollector messageCollector) {
        this.messageCollector = messageCollector;
    }

    @Override
    public void send(OutgoingMessageEnvelope envelope) {
        // extract original envelope values
        SystemStream systemStream = envelope.getSystemStream();
        Object partitionKey = envelope.getPartitionKey();
        String messageText = (String) envelope.getMessage();

        JSONObject message;

        try {
            message = (JSONObject) JSONValue.parseWithException(messageText);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse message: " + messageText);
        }

        addSessionMarkToDataSourceField(message);

        OutgoingMessageEnvelope outgoingMessageEnvelope = new OutgoingMessageEnvelope(systemStream, partitionKey, message.toJSONString());

        messageCollector.send(outgoingMessageEnvelope);
    }

    private void addSessionMarkToDataSourceField(JSONObject message) {
        String dataSource = (String) message.get(DATA_SOURCE_FIELD);

        message.put(DATA_SOURCE_FIELD, dataSource + SESSION_SUFFIX);
    }
}
