package fortscale.streaming.service;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract decorator class of the default Samza MessageColletor.
 * Provides the ability to add additional fields to the message before sending them to the queue
 *
 * @author gils
 * Date: 10/11/2015
 */
public abstract class MessageCollectorGenericDecorator implements MessageCollector {

    protected MessageCollector messageCollector;

    protected Map<String, Object> additionalKeyValueMap = new HashMap<>();

    public MessageCollectorGenericDecorator(MessageCollector messageCollector) {
        this.messageCollector = messageCollector;
    }

    @Override
    public void send(OutgoingMessageEnvelope envelope) {

        if (additionalKeyValueMap.isEmpty()) {
            // nothing to decorate, send the original envelope
            messageCollector.send(envelope);

            return;
        }

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

        for (Map.Entry<String, Object> keyValueEntry : additionalKeyValueMap.entrySet()) {
            message.put(keyValueEntry.getKey(), keyValueEntry.getValue());
        }

        OutgoingMessageEnvelope outgoingMessageEnvelope = new OutgoingMessageEnvelope(systemStream, partitionKey, message.toJSONString());

        messageCollector.send(outgoingMessageEnvelope);
    }
}
