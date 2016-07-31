package fortscale.collection.morphlines.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.MatchIPBuilder.MatchIP;

@RunWith(JUnitParamsRunner.class)
public class MatchIPTest {

	private RecordSinkCommand sink;
	private Config config;

	@Before
	public void setUp() throws Exception {
		sink = new RecordSinkCommand();
		
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.getString("ipAddress")).thenReturn("ipAddress");
		when(config.getString("output")).thenReturn("output");
	}
	
	
	@Test
	@Parameters({ 
		"10.10.10, 192.168.0.3/31, false",
		"not_an_ip_address, 192.168.0.3/31, false",
		"220.78.168.35, 220.78.168.0/21, true",
		"220.78.180.255, 220.78.168.0/21, false",
		"220.78.180.255, 220.78.180.255, true",
		"220.78.180.252, 220.78.180.255, false"
	})
	public void test_match_ip(String ipAddress, String cidr, boolean expectedMatch) {
		
		when(config.getString("cidr")).thenReturn(cidr);

		// build the match ip command
		MatchIPBuilder builder = new MatchIPBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		MatchIP command = (MatchIP) builder.build(config, sink, sink, morphlineContext);
		
		
		// perpare record
		Record record = new Record();
		record.put("ipAddress", ipAddress);
		record.put(MorphlineCommandMonitoringHelper.ITEM_CONTEXT,
				new ItemContext("", null, new MorphlineMetrics(null, "dataSource")));
		
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertNotNull(output.getFirstValue("output"));
		assertEquals(expectedMatch, output.getFirstValue("output"));
	}
	
}
