package fortscale.streaming.task.enrichment;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.UserService;
import fortscale.services.impl.UserServiceImpl;
import fortscale.services.impl.UsernameService;
import fortscale.streaming.cache.KeyValueDbBasedCache;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationConfig;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationService;
import fortscale.streaming.task.KeyValueStoreMock;
import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.StreamingProcessMessageContext;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ContextConfiguration(locations = {"classpath*:META-INF/spring/fortscale-streaming-context-test.xml"})
public class UsernameNormalizationAndTaggingTaskTest {


	public static final String MESSAGE_1 = "{ \"name\": \"user1\",  \"domain\": \"domain\", \"data_source\": \"vpn\", \"last_state\": \"etl\"}";
	public static final String MESSAGE_2 = "{ \"name\": \"user2\",  \"normalized_name\": \"User 2\", \"data_source\": \"vpn\", \"last_state\": \"etl\" }";
	public static final String MESSAGE_3 = "{ \"name\": \"user3\",  \"domain\": \"domain\", \"data_source\": \"vpn\", \"last_state\": \"etl\" }";
	public static final String MESSAGE_4 = "{ \"name\": \"user4\",  \"domain\": \"domain\", \"data_source\": \"vpn\", \"last_state\": \"etl\" }";

	UsernameNormalizationAndTaggingTask task;
	UserService userService;
	UsernameService usernameService;
	SystemStreamPartition systemStreamPartition;
	SystemStream systemStream;
	MessageCollector messageCollector;
	TaskCoordinator taskCoordinator;
	TaskMonitoringHelper taskMonitoringHelper;

	@Before
	public void setUp() throws Exception {

		// Init the task to test
		task = new UsernameNormalizationAndTaggingTask();

		// create the computer service with the levelDB cache
		KeyValueStore<String,Set> userServiceStore = new KeyValueStoreMock<>();
		userService = new UserServiceImpl();

		// create the SensitiveMachine service with the levelDB cache
		KeyValueStore<String,String> usernameStore = new KeyValueStoreMock<>();
		usernameService = new UsernameService();
		usernameService.setCache(new KeyValueDbBasedCache<String, String>(usernameStore, String.class));
		task.topicToServiceMap.put("usernameUpdatesTopic", usernameService);


		// Mocks
		systemStreamPartition = mock(SystemStreamPartition.class);
		systemStream = mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
		messageCollector = mock(MessageCollector.class);
		taskCoordinator = mock(TaskCoordinator.class);
		taskMonitoringHelper = mock(TaskMonitoringHelper.class);
		task.setTaskMonitoringHelper(taskMonitoringHelper);
		//Mockito.when(taskMonitoringHelper.handleUnFilteredEvents(Any)).thenReturn();

		task.dataSourceToConfigurationMap = new HashMap<>();
	}

	@Test
	public void testWrappedProcess() throws Exception {

		// Init the task to test
		UsernameNormalizationAndTaggingTask task = new UsernameNormalizationAndTaggingTask();
		task.createTaskMetrics();

		task.setTaskMonitoringHelper(taskMonitoringHelper);
		// fields
		String normalizedUsernameField = "normalized_name";
		String usernameField = "name";

		// Mocks
		SystemStreamPartition systemStreamPartition = Mockito.mock(SystemStreamPartition.class);
		SystemStream systemStream = Mockito.mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);

		// configuration
		UsernameNormalizationService usernameNormalizationService = Mockito.mock(UsernameNormalizationService.class);
		task.dataSourceToConfigurationMap.put(new StreamingTaskDataSourceConfigKey("vpn", "etl"), new UsernameNormalizationConfig("output1",
				usernameField, "domain", "", normalizedUsernameField, "key", true, "vpn", usernameNormalizationService));


		// User2 with normalized username

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream,"key", MESSAGE_2, "input1");
		ProcessMessageContext contextualMessage = new StreamingProcessMessageContext(envelope, messageCollector , taskCoordinator ,task);
		// run the process on the envelope
		task.processMessage(contextualMessage);

		// validate no normalization for username (we already have it)
		Mockito.verify(usernameNormalizationService, never()).normalizeUsername(eq(anyString()), anyString(), null);
		// validate tagging
		JSONObject message = (JSONObject) JSONValue.parseWithException(MESSAGE_2);


		// User 1 without normalized name, success in normalization

		message = (JSONObject) JSONValue.parseWithException(MESSAGE_1);

		Mockito.when(usernameNormalizationService.normalizeUsername("user1", "domain", null)).thenReturn("User 1");
		Mockito.when(usernameNormalizationService.shouldDropRecord("user1", "User 1")).thenReturn(false);

		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, "key", MESSAGE_1, "input1");
		contextualMessage = new StreamingProcessMessageContext(envelope, messageCollector , taskCoordinator);

		// run the process on the envelope
		task.processMessage(contextualMessage);
		// validate normalization for username
		//Mockito.verify(usernameNormalizationService).normalizeUsername("user1", "domain", message);
		// validate tagging
		message.put(normalizedUsernameField, "User 1");
		//Mockito.verify(task.tagService).addTagsToEvent("User 1", message);


		// User 3 without normalized name, failure in normalization and drop of record

		Mockito.when(usernameNormalizationService.normalizeUsername("user3", "domain", null)).thenReturn(null);
		Mockito.when(usernameNormalizationService.shouldDropRecord("user3", null)).thenReturn(true);
		message = (JSONObject) JSONValue.parseWithException(MESSAGE_3);
		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream,"key", MESSAGE_3, "input1");
		contextualMessage = new StreamingProcessMessageContext(envelope, messageCollector , taskCoordinator );
		// run the process on the envelope
		task.processMessage(contextualMessage);
		// validate normalization for username
		//Mockito.verify(usernameNormalizationService).normalizeUsername("user3", "domain", message, null, false);


		// User 4 without normalized name, failure in normalization and no drop of record

		Mockito.when(usernameNormalizationService.normalizeUsername("user4", "domain", null)).thenReturn(null);
		Mockito.when(usernameNormalizationService.shouldDropRecord("user4", null)).thenReturn(false);
		message = (JSONObject) JSONValue.parseWithException(MESSAGE_4);
		Mockito.when(usernameNormalizationService.getUsernameAsNormalizedUsername("user4", "domain", null))
				.thenReturn("User 4");
		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream,"key", MESSAGE_4, "input1");
		contextualMessage = new StreamingProcessMessageContext(envelope, messageCollector , taskCoordinator );
		// run the process on the envelope
		task.processMessage(contextualMessage);
		// validate normalization for username
		//Mockito.verify(usernameNormalizationService).normalizeUsername("user4", "domain", message);
		//Mockito.verify(usernameNormalizationService).getUsernameAsNormalizedUsername(eq("user4"), eq(any(JSONObject
		//		.class)), null, eq(false));
		// validate tagging
		message.put(normalizedUsernameField, "User 4");
		//Mockito.verify(task.tagService).addTagsToEvent("User 4", message);


	}

	@Test
	public void wrappedProcess_should_add_sensitive_machine_to_sensitiveMachineService_cache() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		// prepare envelope
		String username = "USER_NAME";
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, "key1", mapper.writeValueAsString(username) , "usernameUpdatesTopic");
		ProcessMessageContext contextualMessage = new StreamingProcessMessageContext(envelope, messageCollector, taskCoordinator);

		// run the process on the envelope
		task.processMessage(contextualMessage);
		// validate the tag was added to cache
		assertEquals(username,(String) usernameService.getCache().get("key1"));
	}

	private IncomingMessageEnvelope getIncomingMessageEnvelope(SystemStreamPartition systemStreamPartition,
															   SystemStream systemStream, String key, String message, String topic) {

		IncomingMessageEnvelope envelope = Mockito.mock(IncomingMessageEnvelope.class);
		Mockito.when(envelope.getKey()).thenReturn(key);
		Mockito.when(envelope.getMessage()).thenReturn(message);
		Mockito.when(envelope.getSystemStreamPartition()).thenReturn(systemStreamPartition);
		Mockito.when(systemStream.getStream()).thenReturn(topic);

		return envelope;
	}
}
