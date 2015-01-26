package fortscale.streaming.service.tagging.computer;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ComputerTaggingServiceTest {

	private ComputerTaggingService ComputerTaggingService;

	ComputerService computerService;

	SensitiveMachineService sensitiveMachineService;

	@Before public void setUp() throws Exception {
		Map<String, ComputerTaggingConfig> configs = createConfigurations();
		computerService = mock(ComputerService.class);
		sensitiveMachineService = mock(SensitiveMachineService.class);
		ComputerTaggingService = new ComputerTaggingService(computerService,sensitiveMachineService,configs);

	}

	@Test public void enrichEvent_should_add_security_events_fields() throws Exception {
		JSONObject event = new JSONObject();
		event.put("source_hostname", "source-MY-PC");
		event.put("destination_hostname", "destination-MY-PC");
		when(computerService.getComputerUsageType("source-MY-PC")).thenReturn(ComputerUsageType.Server);
		when(computerService.getClusterGroupNameForHostname("source-MY-PC")).thenReturn("cluster");
		when(sensitiveMachineService.isMachineSensitive("source-MY-PC")).thenReturn(true);
		when(computerService.getComputerUsageType("destination-MY-PC")).thenReturn(ComputerUsageType.Desktop);
		when(computerService.getClusterGroupNameForHostname("destination-MY-PC")).thenReturn("cluster2");
		when(sensitiveMachineService.isMachineSensitive("destination-MY-PC")).thenReturn(false);

		JSONObject enrichedEvent = ComputerTaggingService.enrichEvent("security events input topic", event);

		assertEquals(ComputerUsageType.Server, enrichedEvent.get("source_classification"));
		assertEquals("cluster", enrichedEvent.get("source_clustering"));
		assertEquals(true, enrichedEvent.get("source_isSensitiveMachine"));

		assertEquals(ComputerUsageType.Desktop, enrichedEvent.get("destination_classification"));
		assertEquals("cluster2", enrichedEvent.get("destination_clustering"));
		assertNull(enrichedEvent.get("destination_isSensitiveMachine"));

		verify(computerService,times(1)).ensureComputerExists("source-MY-PC");
		verify(computerService,never()).ensureComputerExists("destination-MY-PC");
	}

	@Test public void enrichEvent_should_add_ssh_fields() throws Exception {
		JSONObject event = new JSONObject();
		event.put("source_hostname1", "source-MY-PC");
		event.put("destination_hostname1", "destination-MY-PC");
		when(computerService.getComputerUsageType("source-MY-PC")).thenReturn(ComputerUsageType.Server);
		when(computerService.getClusterGroupNameForHostname("source-MY-PC")).thenReturn("cluster");
		when(sensitiveMachineService.isMachineSensitive("source-MY-PC")).thenReturn(true);
		when(computerService.getComputerUsageType("destination-MY-PC")).thenReturn(ComputerUsageType.Desktop);
		when(computerService.getClusterGroupNameForHostname("destination-MY-PC")).thenReturn("cluster2");
		when(sensitiveMachineService.isMachineSensitive("destination-MY-PC")).thenReturn(false);

		JSONObject enrichedEvent = ComputerTaggingService.enrichEvent("ssh input topic", event);

		assertEquals(ComputerUsageType.Server, enrichedEvent.get("source_classification1"));
		assertEquals("cluster", enrichedEvent.get("source_clustering1"));
		assertEquals(true, enrichedEvent.get("source_isSensitiveMachine1"));

		assertEquals(ComputerUsageType.Desktop, enrichedEvent.get("destination_classification1"));
		assertEquals("cluster2", enrichedEvent.get("destination_clustering1"));
		assertNull(enrichedEvent.get("destination_isSensitiveMachine1"));

		verify(computerService,times(1)).ensureComputerExists("source-MY-PC");
		verify(computerService,times(1)).ensureComputerExists("destination-MY-PC");
	}

	@Test public void getPartitionKey_should_return_partition_field_value() throws Exception {
		JSONObject event = new JSONObject();
		event.put("userId", "user1");
		assertEquals("user1",ComputerTaggingService.getPartitionKey("security events input topic",event));
	}

	private Map<String, ComputerTaggingConfig> createConfigurations(){
		Map<String, ComputerTaggingConfig> configs = new HashMap<>();
		List<ComputerTaggingFieldsConfig> securityEventsComputerTaggingFieldsConfigs = new ArrayList<>();

		ComputerTaggingFieldsConfig securityEventsSourceComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("source","source_hostname","source_classification","source_clustering","source_isSensitiveMachine",true);
		securityEventsComputerTaggingFieldsConfigs.add(securityEventsSourceComputerTaggingFieldsConfig);

		ComputerTaggingFieldsConfig securityEventsDestinationComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("destination","destination_hostname","destination_classification","destination_clustering",null,false);
		securityEventsComputerTaggingFieldsConfigs.add(securityEventsDestinationComputerTaggingFieldsConfig);

		configs.put("security events input topic", new ComputerTaggingConfig("security events", "security events input topic", "security events output topic", "userId", securityEventsComputerTaggingFieldsConfigs));

		List<ComputerTaggingFieldsConfig> sshComputerTaggingFieldsConfigs = new ArrayList<>();

		ComputerTaggingFieldsConfig sshSourceComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("source","source_hostname1","source_classification1","source_clustering1","source_isSensitiveMachine1",true);
		sshComputerTaggingFieldsConfigs.add(sshSourceComputerTaggingFieldsConfig);

		ComputerTaggingFieldsConfig sshDestinationComputerTaggingFieldsConfig = new ComputerTaggingFieldsConfig("destination","destination_hostname1","destination_classification1","destination_clustering1",null,true);
		sshComputerTaggingFieldsConfigs.add(sshDestinationComputerTaggingFieldsConfig);

		configs.put("ssh input topic", new ComputerTaggingConfig("ssh", "ssh input topic", "ssh output topic", "ssh_userId", sshComputerTaggingFieldsConfigs));
		return configs;
	}
}