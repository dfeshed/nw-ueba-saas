package fortscale.streaming.task.enrichment;

import fortscale.streaming.exceptions.TaskCoordinatorException;
import junit.framework.Assert;
import org.apache.samza.storage.kv.NullSafeKeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.util.ExponentialSleepStrategy;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UserLastActivityTaskTest {

	public static final String MESSAGE_1 = "{ \"name\": \"user1\",  \"time\": 1 }";
	public static final String MESSAGE_2 = "{ \"name\": \"user1\",  \"time\": 2 }";


	@Test
	public void testWrappedProcess() throws Exception {

		// Init the task to test
		UserLastActivityTask task = new UserLastActivityTask();
		task.store = new KeyValueStoreMock<>();
		task.timestampField = "time";
		task.usernameField = "name";
		task.topicToDataSourceMap = new HashMap<>();
		task.topicToDataSourceMap.put("input1" , "vpn");
		task.topicToDataSourceMap.put("input2" , "ssh");

		// Mocks
		SystemStreamPartition systemStreamPartition = Mockito.mock(SystemStreamPartition.class);
		SystemStream systemStream = Mockito.mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);

		// User1, VPN event with time 1

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_1, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		Map<String, Long> user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - VPN event", user1LastActivity);
		assertEquals("User1 - VPN event", new Long(1), user1LastActivity.get("vpn"));

		// User1, VPN event with time 2

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_2, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - VPN event", user1LastActivity);
		assertEquals("User1 - VPN event", new Long(2), user1LastActivity.get("vpn"));
		assertEquals("User1 - SSH event", null, user1LastActivity.get("ssh"));

		// User1, SSH event with time 1

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_1, "input2");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - SSH event", user1LastActivity);
		assertEquals("User1 - SSH event", new Long(1), user1LastActivity.get("ssh"));
		assertEquals("User1 - VPN event", new Long(2), user1LastActivity.get("vpn"));


	}

	private IncomingMessageEnvelope getIncomingMessageEnvelope(SystemStreamPartition systemStreamPartition,
			SystemStream systemStream, String message, String topic) {

		IncomingMessageEnvelope envelope = Mockito.mock(IncomingMessageEnvelope.class);
		Mockito.when(envelope.getMessage()).thenReturn(message);
		Mockito.when(envelope.getSystemStreamPartition()).thenReturn(systemStreamPartition);
		Mockito.when(systemStream.getSystem()).thenReturn(topic);

		return envelope;
	}
}