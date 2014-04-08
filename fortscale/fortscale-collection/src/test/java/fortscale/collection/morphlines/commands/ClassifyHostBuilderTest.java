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
import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;

public class ClassifyHostBuilderTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;
	private ComputerService service;

	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.getString("hostnameField")).thenReturn("hostname");
		when(config.getString("classificationField")).thenReturn("classification");
		
		// mock service
		service = mock(ComputerService.class);
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
	public void classify_should_not_call_service_when_hostname_is_missing() {
		when(service.getComputerUsageType("my-pc")).thenReturn(ComputerUsageType.Unknown);
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
		assertNull(output.getFirstValue("classification"));
		verify(service, times(0)).getComputerUsageType(anyString());
	}
	
	@Test
	public void classify_should_not_call_service_when_hostname_is_empty() {
		when(service.getComputerUsageType("my-pc")).thenReturn(ComputerUsageType.Unknown);
		
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
		assertNull(output.getFirstValue("classification"));
		verify(service, times(0)).getComputerUsageType(anyString());
	}
	
	@Test
	public void classify_should_put_value_given_by_service_in_fields() {
		when(service.getComputerUsageType("my-pc")).thenReturn(ComputerUsageType.Desktop);
		
		// build the classify command
		ClassifyHost command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(ComputerUsageType.Desktop, output.getFirstValue("classification"));
	}
}
