package fortscale.streaming.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    protected ObjectMapper objectMapper;
    protected Map<String, Object> additionalKeyValueMap = new HashMap<>();

    public MessageCollectorGenericDecorator(MessageCollector messageCollector) {
        this.messageCollector = messageCollector;

        objectMapper = new ObjectMapper();
    }

    @Override
    public void send(OutgoingMessageEnvelope envelope) {

        if (additionalKeyValueMap.isEmpty()) {
            // nothing to decorate, send the original envelope
            messageCollector.send(envelope);

            return;
        }
        JSONObject jsonMessage;

        // extract original envelope values
        SystemStream systemStream = envelope.getSystemStream();
        Object partitionKey = envelope.getPartitionKey();

        Object message = envelope.getMessage();
        // minor optimization in case the message is already a jsonObject
        if(message instanceof JSONObject)
        {
            jsonMessage = (JSONObject) message;
        }
        // in case the object is a string -> convert it to json
        else {
            String messageText = (String) envelope.getMessage();

            try {
                jsonMessage = (JSONObject) JSONValue.parseWithException(messageText);
            } catch (ParseException e) {
                throw new RuntimeException("Could not parse message: " + messageText);
            }
        }

        for (Map.Entry<String, Object> keyValueEntry : additionalKeyValueMap.entrySet()) {
            jsonMessage.put(keyValueEntry.getKey(), keyValueEntry.getValue());
        }

        String messageString = null;

        try {
            messageString = objectMapper.writeValueAsString(jsonMessage);
        } catch (JsonProcessingException e) {
            String msg = String.format("error while mapping json=%s to string",jsonMessage.toJSONString());
            throw new RuntimeException(msg,e);
        }

        OutgoingMessageEnvelope outgoingMessageEnvelope = new OutgoingMessageEnvelope(systemStream, partitionKey, messageString);

        messageCollector.send(outgoingMessageEnvelope);
    }
}
