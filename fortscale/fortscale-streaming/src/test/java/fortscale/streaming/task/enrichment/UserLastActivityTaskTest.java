package fortscale.streaming.task.enrichment;


import fortscale.streaming.task.GeneralTaskTest;
import fortscale.streaming.task.KeyValueStoreMock;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserLastActivityTaskTest extends GeneralTaskTest {

	public static final String MESSAGE_1 = "{ \"name\": \"user1\",  \"time\": 1 , \"Status\":\"B\"}";
	public static final String MESSAGE_2 = "{ \"name\": \"user1\",  \"time\": 2 , \"Status\":\"B\"}";
	public static final String MESSAGE_3 = "{ \"name\": \"user1\",  \"time\": 4 , \"Status\":\"C\"}";


	@Test
	public void testWrappedProcess() throws Exception {

		// Init the task to test
		UserLastActivityTask task = new UserLastActivityTask();
		task.store = new KeyValueStoreMock<>();
		task.timestampField = "time";
		task.usernameField = "name";
		task.topicToDataSourceMap = new HashMap<>();
		task.topicToDataSourceMap.put("input1" , new UserLastActivityTask.dataSourceConfiguration("vpn", "Status", "B"));
		task.topicToDataSourceMap.put("input2" , new UserLastActivityTask.dataSourceConfiguration("ssh", "Status", "B"));

		// Mocks
		SystemStreamPartition systemStreamPartition = Mockito.mock(SystemStreamPartition.class);
		SystemStream systemStream = Mockito.mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);

		// User1, VPN event with time 1

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_1, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		Map<String, Long> user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - VPN event", user1LastActivity);
		assertEquals("User1 - VPN event", new Long(1000), user1LastActivity.get("vpn"));

		// User1, VPN event with time 2

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_2, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - VPN event", user1LastActivity);
		assertEquals("User1 - VPN event", new Long(2000), user1LastActivity.get("vpn"));
		assertEquals("User1 - SSH event", null, user1LastActivity.get("ssh"));

		// User1, SSH event with time 1

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_1, "input2");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - SSH event", user1LastActivity);
		assertEquals("User1 - SSH event", new Long(1000), user1LastActivity.get("ssh"));
		assertEquals("User1 - VPN event", new Long(2000), user1LastActivity.get("vpn"));

		// message without success status - shouldn't update last-activity

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_3, "input2");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		user1LastActivity = task.store.get("user1");
		assertNotNull("User1 - SSH event", user1LastActivity);
		assertEquals("User1 - SSH event", new Long(1000), user1LastActivity.get("ssh"));
		assertEquals("User1 - VPN event", new Long(2000), user1LastActivity.get("vpn"));

	}



}

