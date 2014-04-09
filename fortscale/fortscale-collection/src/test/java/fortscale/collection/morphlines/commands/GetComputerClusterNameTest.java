package fortscale.collection.morphlines.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.GetComputerClusterNameBuilder.GetComputerClusterName;
import fortscale.services.ComputerService;

public class GetComputerClusterNameTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;
	private ComputerService service;

	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.getString("hostnameField")).thenReturn("hostname");
		when(config.getString("clusterField")).thenReturn("cluster");
		
		// mock service
		service = mock(ComputerService.class);
	}
	
	private Record getRecord(boolean skipHostname, String hostname) {
		Record record = new Record();
		if (!skipHostname)
			record.put("hostname", hostname);
		return record;
	}
	
	private GetComputerClusterName getCommand() {
		// build the classify command
		GetComputerClusterNameBuilder builder = new GetComputerClusterNameBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return new GetComputerClusterName(builder, config, sink, sink, morphlineContext, service);
	}

	@Test
	public void classify_should_not_call_service_when_hostname_is_missing() {
		GetComputerClusterName command = getCommand();
		Record record = getRecord(true, null);
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("cluster"));
		verify(service, times(0)).getClusterGroupNameForHostname(anyString());
	}
	
	@Test
	public void classify_should_not_call_service_when_hostname_is_empty() {
		GetComputerClusterName command = getCommand();
		Record record = getRecord(false, "");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNull(output.getFirstValue("cluster"));
		verify(service, times(0)).getClusterGroupNameForHostname(anyString());
	}
	
	@Test
	public void classify_should_put_value_given_by_service_in_fields() {
		when(service.getClusterGroupNameForHostname("my-pc")).thenReturn("my-pc");
		
		// build the classify command
		GetComputerClusterName command = getCommand();
		Record record = getRecord(false, "my-pc");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("my-pc", output.getFirstValue("cluster"));
	}
}
