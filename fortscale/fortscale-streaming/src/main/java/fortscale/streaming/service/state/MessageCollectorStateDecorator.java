package fortscale.streaming.service.state;

import fortscale.streaming.service.MessageCollectorGenericDecorator;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;

/**
 * Decorator of the basic MessageCollector. Decorates the message with the task's state
 *
 * @author gils
 * Date: 09/11/2015
 */
public class MessageCollectorStateDecorator extends MessageCollectorGenericDecorator {

    private static final String LAST_STATE_FIELD = "last_state";

    public MessageCollectorStateDecorator(MessageCollector messageCollector) {
        super(messageCollector);
    }

    @Override
    public void send(OutgoingMessageEnvelope envelope) {
        super.send(envelope);
    }

    public void setStreamingTaskMessageState(String streamingTaskMessageState) {
        additionalKeyValueMap.put(LAST_STATE_FIELD, streamingTaskMessageState);
    }
}
