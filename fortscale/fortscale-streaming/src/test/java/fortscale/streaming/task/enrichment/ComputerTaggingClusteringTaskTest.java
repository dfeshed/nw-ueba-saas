package fortscale.streaming.task.enrichment;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Computer;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import fortscale.services.impl.ComputerServiceImpl;
import fortscale.services.computer.SensitiveMachineServiceImpl;
import fortscale.streaming.cache.LevelDbBasedCache;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.tagging.computer.ComputerTaggingService;
import fortscale.streaming.task.GeneralTaskTest;
import fortscale.streaming.task.KeyValueStoreMock;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ComputerTaggingClusteringTaskTest extends GeneralTaskTest {

	final String MESSAGE = "{ \"name\": \"user1\",  \"time\": 1 }";
	final String HOST_NAME = "MY-PC";

	ComputerTaggingClusteringTask task;
	ComputerService computerService;
	SensitiveMachineService sensitiveMachineService;
	SystemStreamPartition systemStreamPartition;
	SystemStream systemStream;
	ObjectMapper mapper = new ObjectMapper();
	MessageCollector messageCollector;
	TaskCoordinator taskCoordinator;


	@Before public void setUp() throws Exception {

		// Init the task to test
		task = new ComputerTaggingClusteringTask();

		// create the computer service with the levelDB cache
		KeyValueStore<String,Computer> computerServiceStore = new KeyValueStoreMock<>();
		computerService = new ComputerServiceImpl();
		computerService.setCache(new LevelDbBasedCache<String, Computer>(computerServiceStore, Computer.class));
		task.topicToServiceMap.put("computerUpdatesTopic", computerService);

		// create the SensitiveMachine service with the levelDB cache
		KeyValueStore<String,String> sensitiveMachineServiceStore = new KeyValueStoreMock<>();
		sensitiveMachineService = new SensitiveMachineServiceImpl();
		sensitiveMachineService.setCache(new LevelDbBasedCache<String, String>(sensitiveMachineServiceStore, String.class));
		task.topicToServiceMap.put("sensitiveMachineUpdatesTopic", sensitiveMachineService);


		// Mocks
		systemStreamPartition = mock(SystemStreamPartition.class);
		systemStream = mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
		messageCollector = mock(MessageCollector.class);
		taskCoordinator = mock(TaskCoordinator.class);
		task.computerTaggingService = mock(ComputerTaggingService.class);

		TaskMonitoringHelper taskMonitoringHelper = mock(TaskMonitoringHelper.class);
		task.setTaskMonitoringHelper(taskMonitoringHelper);

	}

	@Test public void testWrappedInit() throws Exception {

	}

	@Test
	public void wrappedProcess_should_add_updated_computer_to_computerService_cache() throws Exception {
		// prepare envelope
		Computer updateComputer = new Computer();
		updateComputer.setName(HOST_NAME);

		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, "key1", mapper.writeValueAsString(updateComputer) , "computerUpdatesTopic");
		// run the process on the envelope
		task.wrappedProcess(envelope , messageCollector, taskCoordinator);
		// validate the computer was added to cache
		// Need to check the name and can't use computer equals since the base class AbstractDocument uses in it's equals condition on id field not been null, and it's can't be set from outside (only by MongoDB)
		assertEquals(updateComputer.getName(), ((Computer) computerService.getCache().get("key1")).getName());
	}

	@Test
	public void wrappedProcess_should_add_sensitive_machine_to_sensitiveMachineService_cache() throws Exception {
		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, HOST_NAME, mapper.writeValueAsString(HOST_NAME)  , "sensitiveMachineUpdatesTopic");
		// run the process on the envelope
		task.wrappedProcess(envelope , messageCollector, taskCoordinator);
		// validate the sensitiveMachine was added to cache
		assertEquals(HOST_NAME, sensitiveMachineService.getCache().get(HOST_NAME));
	}

	@Test
	public void wrappedProcess_should_delete_sensitive_machine_from_sensitiveMachineService_cache() throws Exception {
		//add sensitive machine to cache - so we have something to remove
		sensitiveMachineService.getCache().put(HOST_NAME, HOST_NAME);
		//check the sensitiveMachine is in the cache
		assertEquals(HOST_NAME, sensitiveMachineService.getCache().get(HOST_NAME));

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, HOST_NAME, null, "sensitiveMachineUpdatesTopic");

		// run the process on the envelope
		task.wrappedProcess(envelope , messageCollector, taskCoordinator);
		// validate the sensitiveMachine is removed fom the cache
		assertNull(sensitiveMachineService.getCache().get(HOST_NAME));
	}

	@Test
	public void wrappedProcess_should_enriched_the_message_and_send_to_output_topic() throws Exception {

		//arrange the enrich mock
		final Answer answer = new Answer<JSONObject>() {
			@Override public JSONObject answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				JSONObject event = null;
				if (arguments != null &&
						arguments.length > 0 &&
						arguments[0] != null) {
					event = (JSONObject) arguments[1];
					event.put("enrichedKey", "enrichedValue");
				}
				return event;
			}
		};
		doAnswer(answer).when(task.computerTaggingService).enrichEvent(anyString(),any(JSONObject.class));

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null, MESSAGE  , "sshInputTopic");

		// run the process on the envelope
		task.wrappedProcess(envelope ,messageCollector, taskCoordinator);
		// verify the enriched message send to output topic
		ArgumentCaptor<OutgoingMessageEnvelope> argument = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);
		verify(messageCollector).send(argument.capture());
		JSONObject event = (JSONObject) JSONValue.parseWithException((String) argument.getValue().getMessage());
		assertEquals("enrichedValue", event.get("enrichedKey"));
	}



	@Test public void testWrappedClose() throws Exception {

	}

}