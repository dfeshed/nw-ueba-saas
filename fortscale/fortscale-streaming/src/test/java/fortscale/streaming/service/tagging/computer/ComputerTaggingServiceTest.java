package fortscale.streaming.service.tagging.computer;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ComputerTaggingServiceTest {

	private ComputerTaggingService computerTaggingService;

	ComputerService computerService;

	SensitiveMachineService sensitiveMachineService;
	Map<StreamingTaskDataSourceConfigKey, ComputerTaggingConfig> configs;


	@Before public void setUp() throws Exception {
		configs = createConfigurations();
		computerService = mock(ComputerService.class);
		sensitiveMachineService = mock(SensitiveMachineService.class);
		computerTaggingService = new ComputerTaggingService(computerService,sensitiveMachineService,configs);

	}

	@Test public void enrichEvent_should_add_security_events_fields() throws Exception {
		JSONObject event = new JSONObject();
		event.put("source_hostname", "source-MY-PC");
		event.put("destination_hostname", "destination-MY-PC");
		when(computerService.getComputerUsageType("source-MY-PC")).thenReturn(ComputerUsageType.Server);
		when(sensitiveMachineService.isMachineSensitive("source-MY-PC")).thenReturn(true);
		when(computerService.getComputerUsageType("destination-MY-PC")).thenReturn(ComputerUsageType.Desktop);
		when(sensitiveMachineService.isMachineSensitive("destination-MY-PC")).thenReturn(false);

		JSONObject enrichedEvent = computerTaggingService.enrichEvent(new ComputerTaggingConfig("login", "lastState", "ssh output topic", "ssh_userId",configs.get(new StreamingTaskDataSourceConfigKey("login","lastState")).getComputerTaggingFieldsConfigList()), event);

		assertEquals(ComputerUsageType.Server, enrichedEvent.get("source_classification"));
		assertEquals(true, enrichedEvent.get("source_isSensitiveMachine"));

		assertEquals(ComputerUsageType.Desktop, enrichedEvent.get("destination_classification"));
		assertNull(enrichedEvent.get("destination_isSensitiveMachine"));

		verify(computerService,times(1)).ensureComputerExists("source-MY-PC");
		verify(computerService,never()).ensureComputerExists("destination-MY-PC");
	}

	@Test public void enrichEvent_should_add_ssh_fields() throws Exception {
		JSONObject event = new JSONObject();
		event.put("source_hostname1", "source-MY-PC");
		event.put("destination_hostname1", "destination-MY-PC");
		when(computerService.getComputerUsageType("source-MY-PC")).thenReturn(ComputerUsageType.Server);
		when(sensitiveMachineService.isMachineSensitive("source-MY-PC")).thenReturn(true);
		when(computerService.getComputerUsageType("destination-MY-PC")).thenReturn(ComputerUsageType.Desktop);
		when(sensitiveMachineService.isMachineSensitive("destination-MY-PC")).thenReturn(false);

		JSONObject enrichedEvent = computerTaggingService.enrichEvent(new ComputerTaggingConfig("ssh", "lastState", "ssh output topic", "ssh_userId",configs.get(new StreamingTaskDataSourceConfigKey("ssh","lastState")).getComputerTaggingFieldsConfigList()), event);

		assertEquals(ComputerUsageType.Server, enrichedEvent.get("source_classification1"));
		assertEquals(true, enrichedEvent.get("source_isSensitiveMachine1"));

		assertEquals(ComputerUsageType.Desktop, enrichedEvent.get("destination_classification1"));
		assertNull(enrichedEvent.get("destination_isSensitiveMachine1"));

		verify(computerService,times(1)).ensureComputerExists("source-MY-PC");
		verify(computerService,times(1)).ensureComputerExists("destination-MY-PC");
	}

	@Test public void getPartitionKey_should_return_partition_field_value() throws Exception {
		JSONObject event = new JSONObject();
		event.put("userId", "user1");
		assertEquals("user1", computerTaggingService.getPartitionKey(new StreamingTaskDataSourceConfigKey("login","lastState"), event));
	}

	@Test(expected =  Exception.class)
	public void getOutputTopic_should_throw_exception_in_case_input_topic_not_defined_in_configuration() {
		computerTaggingService.getOutputTopic(new StreamingTaskDataSourceConfigKey("dataSource","lastState"));
	}

	private Map<StreamingTaskDataSourceConfigKey, ComputerTaggingConfig> createConfigurations(){
		Map<StreamingTaskDataSourceConfigKey, ComputerTaggingConfig> configs = new HashMap<>();
		List<ComputerTaggingFieldsConfig> securityEventsComputerTaggingFieldsConfigs = new ArrayList<>();

		ComputerTaggingFieldsConfig securityEventsSourceComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("source","source_hostname","source_classification","source_isSensitiveMachine",true);
		securityEventsComputerTaggingFieldsConfigs.add(securityEventsSourceComputerTaggingFieldsConfig);

		ComputerTaggingFieldsConfig securityEventsDestinationComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("destination","destination_hostname","destination_classification",null,false);
		securityEventsComputerTaggingFieldsConfigs.add(securityEventsDestinationComputerTaggingFieldsConfig);

		configs.put(new StreamingTaskDataSourceConfigKey("login","lastState"), new ComputerTaggingConfig("security events", "last state", "security events output topic", "userId", securityEventsComputerTaggingFieldsConfigs));

		List<ComputerTaggingFieldsConfig> sshComputerTaggingFieldsConfigs = new ArrayList<>();

		ComputerTaggingFieldsConfig sshSourceComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("source","source_hostname1","source_classification1","source_isSensitiveMachine1",true);
		sshComputerTaggingFieldsConfigs.add(sshSourceComputerTaggingFieldsConfig);

		ComputerTaggingFieldsConfig sshDestinationComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("destination","destination_hostname1","destination_classification1",null,true);
		sshComputerTaggingFieldsConfigs.add(sshDestinationComputerTaggingFieldsConfig);

		configs.put(new StreamingTaskDataSourceConfigKey("ssh","lastState"), new ComputerTaggingConfig("ssh", "last state", "ssh output topic", "ssh_userId", sshComputerTaggingFieldsConfigs));
		return configs;
	}
}