package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.GetTimezoneBuilder.GetTimezone;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTimezoneBuilderTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;


	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);

		when(config.getString("sourceType")).thenReturn("sourceType");
        when(config.hasPath("hostnameField")).thenReturn(true);
		when(config.getString("hostnameField")).thenReturn("hostname");
        when(config.getString("")).thenReturn("hostname");
		when(config.getString("timezoneOutputField")).thenReturn("timezoneOutput");
		
	}
	
	private Record getRecord(String host) {
		Record record = new Record();
		record.put("hostname", host);
		record.put(MorphlineCommandMonitoringHelper.ITEM_CONTEXT,
				new ItemContext("", null, new MorphlineMetrics(null, "dataSource")));
		return record;
	}

	@SuppressWarnings("unchecked")
	private GetTimezone getCommand(String sourceType, String timezones) {
		if(sourceType != null){
			when(config.getString("sourceType")).thenReturn(sourceType);
		} else{
			when(config.getString("sourceType")).thenThrow(ConfigException.Missing.class);
		}
		GetTimezoneBuilder builder = new GetTimezoneBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return new GetTimezone(builder, config, sink, sink, morphlineContext,timezones);
	}



	@Test
	public void command_returns_vpn_timezone() {
        Record record = getRecord("il.srvvpn01");
		GetTimezone command = getCommand("vpn","{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");

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
		assertEquals("UTC", output.getFirstValue("timezoneOutput"));
	}

	@Test(expected=ConfigException.Missing.class)
	public void source_type_is_null() {		
		getCommand(null, "{  \"regexpList\": [    {\"type\" : \"vpn\" , \"host\" : \"il.srv.+\" , \"timezone\" : \"Asia/Jerusalem\"},    {\"type\" : \"4769\" , \"host\" : \"sm.srv.+\" , \"timezone\" : \"Pacific/Samoa\"}  ], \"defaultTimezone\" : \"UTC\" }");
		
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
		assertEquals("UTC", output.getFirstValue("timezoneOutput"));
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
		assertEquals("UTC", output.getFirstValue("timezoneOutput"));
	}

}
