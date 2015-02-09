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

public class UserMongoUpdateTaskTest extends GeneralTaskTest {

	public static final String MESSAGE_1 = "{ \"name\": \"user1\",\"username\": \"user1\", \"time\": 1 , \"Status\":\"B\"}";
	public static final String MESSAGE_2 = "{ \"name\": \"user1\",\"username\": \"user1\",  \"time\": 2 , \"Status\":\"B\"}";
	public static final String MESSAGE_3 = "{ \"name\": \"user1\",\"username\": \"user1\",  \"time\": 4 , \"Status\":\"C\"}";


	@Test
	public void testWrappedProcess() throws Exception {

		// Init the task to test
		UserMongoUpdateTask task = new UserMongoUpdateTask();
		task.store = new KeyValueStoreMock<>();
		task.timestampField = "time";
		task.usernameField = "name";

		Map<String,Boolean> updateOnlyPerClassifire = new HashMap<>();
		updateOnlyPerClassifire.put("vpn",true);
		updateOnlyPerClassifire.put("ssh",true);
		updateOnlyPerClassifire.put("login", false);
		task.updateOnlyPerClassifire = updateOnlyPerClassifire;



		task.topicToDataSourceMap = new HashMap<>();
		task.topicToDataSourceMap.put("input1" , new UserMongoUpdateTask.dataSourceConfiguration("vpn", "Status", "B",true,"username"));
		task.topicToDataSourceMap.put("input2" , new UserMongoUpdateTask.dataSourceConfiguration("ssh", "Status", "B",true,"username"));
		task.topicToDataSourceMap.put("input3" , new UserMongoUpdateTask.dataSourceConfiguration("login", "Status", "B",true,"account_name"));

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
		UserInfoForUpdate userInfo1 = task.store.get("user1");
		assertNotNull("User1 - VPN event", userInfo1);
		assertEquals("User1 - VPN event", new Long(1000), userInfo1.getUserInfo().get("vpn").getKey());
		assertEquals("User1 - VPN event", "user1", userInfo1.getUserInfo().get("vpn").getValue());

		// User1, VPN event with time 2

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_2, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		userInfo1 = task.store.get("user1");
		assertNotNull("User1 - VPN event", userInfo1);
		assertEquals("User1 - VPN event", new Long(2000), userInfo1.getUserInfo().get("vpn").getKey());
		assertEquals("User1 - VPN event", null,userInfo1.getUserInfo().get("ssh"));
		assertEquals("User1 - VPN event", "user1", userInfo1.getUserInfo().get("vpn").getValue());

		// User1, SSH event with time 1

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_1, "input2");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		userInfo1 = task.store.get("user1");
		assertNotNull("User1 - SSH event", userInfo1);
		assertEquals("User1 - SSH event", new Long(1000), userInfo1.getUserInfo().get("ssh").getKey());
		assertEquals("User1 - VPN event", new Long(2000), userInfo1.getUserInfo().get("vpn").getKey());
		assertEquals("User1 - SSH event", "user1", userInfo1.getUserInfo().get("ssh").getValue());
		assertEquals("User1 - VPN event", "user1", userInfo1.getUserInfo().get("vpn").getValue());

		// message without success status - shouldn't update last-activity

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_3, "input2");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		userInfo1 = task.store.get("user1");
		assertNotNull("User1 - SSH event", userInfo1);
		assertEquals("User1 - SSH event", new Long(1000), userInfo1.getUserInfo().get("ssh").getKey());
		assertEquals("User1 - VPN event", new Long(2000), userInfo1.getUserInfo().get("vpn").getKey());

	}



}

