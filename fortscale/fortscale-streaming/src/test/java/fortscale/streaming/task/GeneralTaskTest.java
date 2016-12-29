package fortscale.streaming.task;

import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.StreamingProcessMessageContext;
import net.minidev.json.parser.ParseException;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.mockito.Mockito;

/**
 * Basic functionality for task unit testing
 */
public class GeneralTaskTest {

	protected ProcessMessageContext getFSProcessContextualMessage(SystemStreamPartition systemStreamPartition,
																  SystemStream systemStream, String key, String message,
																  String topic, MessageCollector collector,
																  TaskCoordinator coordinator, AbstractStreamTask streamTask) throws ParseException {
		return new StreamingProcessMessageContext(getIncomingMessageEnvelope(systemStreamPartition,systemStream,key,message,topic), collector , coordinator,streamTask);
	}
	protected IncomingMessageEnvelope getIncomingMessageEnvelope(SystemStreamPartition systemStreamPartition,
			SystemStream systemStream, String key, String message, String topic) {

		IncomingMessageEnvelope envelope = Mockito.mock(IncomingMessageEnvelope.class);
		Mockito.when(envelope.getMessage()).thenReturn(message);
		Mockito.when(envelope.getKey()).thenReturn(key);
		Mockito.when(envelope.getSystemStreamPartition()).thenReturn(systemStreamPartition);
		Mockito.when(systemStream.getStream()).thenReturn(topic);

		return envelope;
	}
}
