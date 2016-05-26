package fortscale.streaming.task.enrichment;


import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.task.GeneralTaskTest;
import fortscale.streaming.task.KeyValueStoreMock;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = {"classpath*:META-INF/spring/fortscale-streaming-context-test.xml"})
public class UserMongoUpdateTaskTest extends GeneralTaskTest {

	public static final String MESSAGE_1 = "{ \"name\": \"user1\",\"username\": \"user1\", \"time\": 1 , \"Status\":\"B\", \"last_state\":\"state1\", \"data_source\":\"vpn\"}";
	public static final String MESSAGE_2 = "{ \"name\": \"user1\",\"username\": \"user1\",  \"time\": 2 , \"Status\":\"B\", \"last_state\":\"state1\", \"data_source\":\"ssh\"}";
	public static final String MESSAGE_3 = "{ \"name\": \"user1\",\"username\": \"user1\",  \"time\": 4 , \"Status\":\"C\", \"last_state\":\"state1\", \"data_source\":\"login\"}";


	@Test
	public void testWrappedProcess() throws Exception {

		// Init the task to test
		UserMongoUpdateTask task = new UserMongoUpdateTask();
		task.createAbstractTaskMetrics();
		task.store = new KeyValueStoreMock<>();
		task.timestampField = "time";
		task.usernameField = "name";

		Map<String,Boolean> updateOnlyPerClassifire = new HashMap<>();
		updateOnlyPerClassifire.put("vpn",true);
		updateOnlyPerClassifire.put("ssh",true);
		updateOnlyPerClassifire.put("login", false);
		task.updateOnlyPerClassifier = updateOnlyPerClassifire;



		task.dataSourceConfigs = new HashMap<>();
		task.dataSourceConfigs.put(new StreamingTaskDataSourceConfigKey("vpn","state1") , new UserMongoUpdateTask.DataSourceConfiguration("vpn", "Status", "B",true,"username"));
		task.dataSourceConfigs.put(new StreamingTaskDataSourceConfigKey("ssh","state1"), new UserMongoUpdateTask.DataSourceConfiguration("ssh", "Status", "B",true,"username"));
		task.dataSourceConfigs.put(new StreamingTaskDataSourceConfigKey("login","state1") , new UserMongoUpdateTask.DataSourceConfiguration("login", "Status", "B",true,"account_name"));

		// Mocks
		SystemStreamPartition systemStreamPartition = Mockito.mock(SystemStreamPartition.class);
		SystemStream systemStream = Mockito.mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
		TaskMonitoringHelper taskMonitoringHelper = Mockito.mock(TaskMonitoringHelper.class);
		task.setTaskMonitoringHelper(taskMonitoringHelper);
		// User1, VPN event with time 1

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_1, "vpn");
		// run the process on the envelope
		task.wrappedProcess(envelope, Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		UserInfoForUpdate userInfo1 = task.store.get("user1");
		assertNotNull("User1 - VPN event", userInfo1);
		assertEquals("User1 - VPN event", new Long(1000), userInfo1.getUserInfo().get("vpn").getKey());
		assertEquals("User1 - VPN event", "user1", userInfo1.getUserInfo().get("vpn").getValue());

		// User1, VPN event with time 2

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_2, "ssh");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		userInfo1 = task.store.get("user1");
		assertNotNull("User1 - VPN event", userInfo1);
		assertEquals("User1 - VPN event", new Long(1000), userInfo1.getUserInfo().get("vpn").getKey());
		assertEquals("User1 - VPN event", new Long(2000) ,userInfo1.getUserInfo().get("ssh").getKey());
		assertEquals("User1 - VPN event", "user1", userInfo1.getUserInfo().get("vpn").getValue());

		// User1, SSH event with time 1



		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE_3, "login");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate the last-activity map
		userInfo1 = task.store.get("user1");
		assertNotNull("User1 - SSH event", userInfo1);
		assertEquals("User1 - SSH event", new Long(1000), userInfo1.getUserInfo().get("vpn").getKey());
		assertEquals("User1 - VPN event", new Long(2000), userInfo1.getUserInfo().get("ssh").getKey());
		//Login event filtered out because in the configuration StreamingTaskDataSourceConfigKey("login","state1")
		//the logUserNameField equals to "account_name" and message3 doesn't have "account_name" field
		assertEquals("User1 - Login event", null, userInfo1.getUserInfo().get("login"));

	}



}

