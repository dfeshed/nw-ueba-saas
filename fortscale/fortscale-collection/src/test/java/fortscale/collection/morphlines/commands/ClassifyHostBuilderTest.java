package fortscale.collection.morphlines.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.ClassifyHostBuilder.ClassifyHost;
import fortscale.services.machine.EndpointDetectionService;
import fortscale.services.machine.MachineInfo;

public class ClassifyHostBuilderTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;
	private EndpointDetectionService service;

	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.getString("hostnameField")).thenReturn("hostname");
		
		// mock service
		service = mock(EndpointDetectionService.class);
	}
	
	private Record getRecord(boolean skipHostname, String hostname) {
		Record record = new Record();
		if (!skipHostname)
			record.put("hostname", hostname);
		return record;
	}
	
	private ClassifyHost getCommand() {
		// build the classify command
		ClassifyHostBuilder builder = new ClassifyHostBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return  new ClassifyHost(builder, config, sink, sink, morphlineContext, service);
	}
	
	@Test
	public void classify_should_not_output_is_server_when_not_requested() {
		when(service.getMachineInfo("my-pc")).thenReturn(new MachineInfo("my-pc", true, false));
		
		// build the classify command
		when(config.hasPath("isEndpointField")).thenReturn(true);
		when(config.getString("isEndpointField")).thenReturn("isEndpoint");
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("isServer"));
		assertNotNull(output.getFirstValue("isEndpoint"));
	}
	
	@Test
	public void classify_should_not_output_is_endpoint_when_not_requested() {
		when(service.getMachineInfo("my-pc")).thenReturn(new MachineInfo("my-pc", true, false));
		
		// build the classify command
		when(config.hasPath("isServerField")).thenReturn(true);
		when(config.getString("isServerField")).thenReturn("isServer");
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNotNull(output.getFirstValue("isServer"));
		assertNull(output.getFirstValue("isEndpoint"));
	}
	
	@Test
	public void classify_should_not_output_any_field_when_not_requested() {
		when(service.getMachineInfo("my-pc")).thenReturn(new MachineInfo("my-pc", true, false));
		
		// build the classify command
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("isServer"));
		assertNull(output.getFirstValue("isEndpoint"));
	}
	
	@Test
	public void classify_should_put_null_values_when_service_did_not_find_machine_info() {
		when(service.getMachineInfo("my-pc")).thenReturn(null);
		
		// build the classify command
		when(config.hasPath("isEndpointField")).thenReturn(true);
		when(config.hasPath("isServerField")).thenReturn(true);
		when(config.getString("isEndpointField")).thenReturn("isEndpoint");
		when(config.getString("isServerField")).thenReturn("isServer");
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("isServer"));
		assertNull(output.getFirstValue("isEndpoint"));
	}

	@Test
	public void classify_should_not_call_service_when_hostname_is_missing() {
		when(service.getMachineInfo("my-pc")).thenReturn(null);
		// build the classify command
		when(config.hasPath("isEndpointField")).thenReturn(true);
		when(config.hasPath("isServerField")).thenReturn(true);
		when(config.getString("isEndpointField")).thenReturn("isEndpoint");
		when(config.getString("isServerField")).thenReturn("isServer");
		ClassifyHost command = getCommand();
		Record record = getRecord(true, null);
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("isServer"));
		assertNull(output.getFirstValue("isEndpoint"));
		verify(service, times(0)).getMachineInfo(anyString());
	}
	
	@Test
	public void classify_should_not_call_service_when_hostname_is_empty() {
		when(service.getMachineInfo("my-pc")).thenReturn(new MachineInfo("my-pc", true, false));
		
		// build the classify command
		when(config.hasPath("isEndpointField")).thenReturn(true);
		when(config.hasPath("isServerField")).thenReturn(true);
		when(config.getString("isEndpointField")).thenReturn("isEndpoint");
		when(config.getString("isServerField")).thenReturn("isServer");
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("isServer"));
		assertNull(output.getFirstValue("isEndpoint"));
		verify(service, times(0)).getMachineInfo(anyString());
	}
	
	@Test
	public void classify_should_put_value_given_by_service_in_fields() {
		when(service.getMachineInfo("my-pc")).thenReturn(new MachineInfo("my-pc", true, false));
		
		// build the classify command
		when(config.hasPath("isEndpointField")).thenReturn(true);
		when(config.hasPath("isServerField")).thenReturn(true);
		when(config.getString("isEndpointField")).thenReturn("isEndpoint");
		when(config.getString("isServerField")).thenReturn("isServer");
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(false, output.getFirstValue("isServer"));
		assertEquals(true, output.getFirstValue("isEndpoint"));
	}
}
