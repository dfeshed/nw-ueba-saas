package fortscale.collection.morphlines.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.GetTimezoneBuilder.GetTimezone;

public class GetTimezoneBuilderTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;

	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.getString("sourceType")).thenReturn("sourceType");
		when(config.getString("hostnameField")).thenReturn("hostname");
		when(config.getString("timezoneOutputField")).thenReturn("timezoneOutput");
		
	}
	
	private Record getRecord(String host) {
		Record record = new Record();
		record.put("hostname", host);
		return record;
	}

	private GetTimezone getCommand(String sourceType, String timezones) {
		when(config.getString("sourceType")).thenReturn(sourceType);
		GetTimezoneBuilder builder = new GetTimezoneBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return new GetTimezone(builder, config, sink, sink, morphlineContext,timezones);
	}
	
	@Test
	public void command_returns_vpn_timezone() {		
		GetTimezone command = getCommand("vpn","{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord("il.srvvpn01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("Asia/Jerusalem", output.getFirstValue("timezoneOutput"));	
	}

	@Test
	public void command_returns_4769_timezone() {		
		GetTimezone command = getCommand("4769", "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord("sm.srvvpn01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("Pacific/Samoa", output.getFirstValue("timezoneOutput"));	
	}
	
	@Test
	public void command_returns_default_timezone() {		
		GetTimezone command = getCommand("ssh", "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord("srvssh01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("UTC", output.getFirstValue("timezoneOutput"));	
	}

	@Test
	public void hostname_is_empty() {		
		GetTimezone command = getCommand("vpn", "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord("");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("UTC", output.getFirstValue("timezoneOutput"));	
	}

	@Test
	public void source_type_is_empty() {		
		GetTimezone command = getCommand("", "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord("ilsrv01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("UTC", output.getFirstValue("timezoneOutput"));	
	}

	
	@Test
	public void hostname_is_null() {		
		GetTimezone command = getCommand("vpn", "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord(null);
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(null, output.getFirstValue("timezoneOutput"));	
	}

	@Test
	public void source_type_is_null() {		
		GetTimezone command = getCommand(null, "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		Record record = getRecord("ilsrv01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(null, output.getFirstValue("timezoneOutput"));	
	}
	
	@Test
	public void no_timezones_config() {		
		GetTimezone command = getCommand("vpn", "");
		Record record = getRecord("il.srvvpn01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("Asia/Jerusalem", output.getFirstValue("timezoneOutput"));	
	}
	
	@Test
	public void timezones_config_is_null() {
		GetTimezone command = getCommand("vpn", null);
		Record record = getRecord("il.srvvpn01");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("Asia/Jerusalem", output.getFirstValue("timezoneOutput"));	
	}

}
