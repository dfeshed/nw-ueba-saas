package fortscale.collection.morphlines.commands;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
import fortscale.collection.morphlines.commands.ParseKerberosTicketOptionsBuilder.ParseKerberosTicketOptions;


@RunWith(JUnitParamsRunner.class)
public class ParseKerberosTicketOptionsTest {

	private RecordSinkCommand sink;
	private ParseKerberosTicketOptions command;
	private Config config;
	private MorphlineContext morphlineContext;
	
	@Before
	public void setUp() throws Exception {
		sink = new RecordSinkCommand();
		morphlineContext = new MorphlineContext.Builder().build();
		config = mock(Config.class);
		when(config.getString("ticketOptionsField")).thenReturn("ticketOptions");
	}


	@Test
	public void parse_ticket_with_only_ticket_options_should_not_output_fields() {
		// build the parse kerberos command
		ParseKerberosTicketOptionsBuilder builder = new ParseKerberosTicketOptionsBuilder();
		command = (ParseKerberosTicketOptions) builder.build(config, sink, sink, morphlineContext);
		
		// perpare record
		Record record = getRecord("0x40810010");
	
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertFalse(isRecordHasField(output, "forwardable"));
		assertFalse(isRecordHasField(output, "forwarded"));
		assertFalse(isRecordHasField(output, "proxied"));
		assertFalse(isRecordHasField(output, "postdated"));
		assertFalse(isRecordHasField(output, "renewRequest"));
		assertFalse(isRecordHasField(output, "constraintDelegation"));
	}

	private Record getRecord(String ticketOptions) {
		Record record = new Record();
		if (ticketOptions!=null){
			record.put("ticketOptions", ticketOptions);
		}
		record.put(MorphlineCommandMonitoringHelper.ITEM_CONTEXT,
				new ItemContext("", null, new MorphlineMetrics(null, "dataSource")));
		return record;
	}

	@Test
	public void parse_ticket_with_no_ticket_field_should_not_output_fields() {
		// build the parse kerberos command
		ParseKerberosTicketOptionsBuilder builder = new ParseKerberosTicketOptionsBuilder();
		when(config.hasPath("forwardableField")).thenReturn(true);
		when(config.getString("forwardableField")).thenReturn("forwardable");
		command = (ParseKerberosTicketOptions) builder.build(config, sink, sink, morphlineContext);
		
		// perpare record
		Record record = getRecord(null);
	
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertFalse(isRecordHasField(output, "forwardable"));
		assertFalse(isRecordHasField(output, "forwarded"));
		assertFalse(isRecordHasField(output, "proxied"));
		assertFalse(isRecordHasField(output, "postdated"));
		assertFalse(isRecordHasField(output, "renewRequest"));
		assertFalse(isRecordHasField(output, "constraintDelegation"));
	}
	
	@Test
	public void parse_ticket_with_no_forwardable_field_but_value_is_parameter_should_output_false() {
		// build the parse kerberos command
		ParseKerberosTicketOptionsBuilder builder = new ParseKerberosTicketOptionsBuilder();
		when(config.hasPath("forwardableField")).thenReturn(true);
		when(config.getString("forwardableField")).thenReturn("forwardable");
		command = (ParseKerberosTicketOptions) builder.build(config, sink, sink, morphlineContext);
		
		// perpare record
		Record record = getRecord("0x20000000");
	
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertTrue(isRecordHasField(output, "forwardable"));
		assertTrue(output.getFirstValue("forwardable").equals("False"));
		assertFalse(isRecordHasField(output, "forwarded"));
		assertFalse(isRecordHasField(output, "proxied"));
		assertFalse(isRecordHasField(output, "postdated"));
		assertFalse(isRecordHasField(output, "renewRequest"));
		assertFalse(isRecordHasField(output, "constraintDelegation"));
	}
	
	@Test
	public void parse_ticket_with_proxied_field_but_bit_set_in_should_not_output_fields() {
		// build the parse kerberos command
		ParseKerberosTicketOptionsBuilder builder = new ParseKerberosTicketOptionsBuilder();
		when(config.hasPath("proxiedField")).thenReturn(true);
		when(config.getString("proxiedField")).thenReturn("proxied");
		command = (ParseKerberosTicketOptions) builder.build(config, sink, sink, morphlineContext);
		
		// perpare record
		Record record = getRecord("0x40810010");
	
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertFalse(isRecordHasField(output, "forwardable"));
		assertFalse(isRecordHasField(output, "forwarded"));
		assertTrue(isRecordHasField(output, "proxied"));
		assertTrue(output.getFirstValue("proxied").equals("False"));
		assertFalse(isRecordHasField(output, "postdated"));
		assertFalse(isRecordHasField(output, "renewRequest"));
		assertFalse(isRecordHasField(output, "constraintDelegation"));
	}
	
	@Test
	public void parse_ticket_with_all_fields_set_should_output_them_all() {
		// build the parse kerberos command
		ParseKerberosTicketOptionsBuilder builder = new ParseKerberosTicketOptionsBuilder();
		when(config.hasPath("forwardableField")).thenReturn(true);
		when(config.getString("forwardableField")).thenReturn("forwardable");
		when(config.hasPath("forwardedField")).thenReturn(true);
		when(config.getString("forwardedField")).thenReturn("forwarded");
		when(config.hasPath("proxiedField")).thenReturn(true);
		when(config.getString("proxiedField")).thenReturn("proxied");
		when(config.hasPath("postdatedField")).thenReturn(true);
		when(config.getString("postdatedField")).thenReturn("postdated");
		when(config.hasPath("renewRequestField")).thenReturn(true);
		when(config.getString("renewRequestField")).thenReturn("renewRequest");
		when(config.hasPath("constraintDelegationField")).thenReturn(true);
		when(config.getString("constraintDelegationField")).thenReturn("constraintDelegation");
		command = (ParseKerberosTicketOptions) builder.build(config, sink, sink, morphlineContext);
		
		// perpare record
		Record record = getRecord("0xFFFFFFFF");
	
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertTrue(isRecordHasField(output, "forwardable"));
		assertTrue(isRecordHasField(output, "forwarded"));
		assertTrue(isRecordHasField(output, "proxied"));
		assertTrue(isRecordHasField(output, "postdated"));
		assertTrue(isRecordHasField(output, "renewRequest"));
		assertTrue(isRecordHasField(output, "constraintDelegation"));
	}
	
	@Test
	@Parameters({ 
		"forwardable, 0x40000000",
		"forwarded,   0x20000000",
		"proxied,     0x08000000",
		"postdated,   0x02000000",
		"renewRequest,0x00000002",
		"constraintDelegation,0x00020000",
		"renewRequest,0x10002"
	})
	public void test_parse_ticket_with_field_option_set_bit(String field, String ticket) {
		// build the parse kerberos command
		ParseKerberosTicketOptionsBuilder builder = new ParseKerberosTicketOptionsBuilder();
		when(config.hasPath("forwardableField")).thenReturn(true);
		when(config.getString("forwardableField")).thenReturn("forwardable");
		when(config.hasPath("forwardedField")).thenReturn(true);
		when(config.getString("forwardedField")).thenReturn("forwarded");
		when(config.hasPath("proxiedField")).thenReturn(true);
		when(config.getString("proxiedField")).thenReturn("proxied");
		when(config.hasPath("postdatedField")).thenReturn(true);
		when(config.getString("postdatedField")).thenReturn("postdated");
		when(config.hasPath("renewRequestField")).thenReturn(true);
		when(config.getString("renewRequestField")).thenReturn("renewRequest");
		when(config.hasPath("constraintDelegationField")).thenReturn(true);
		when(config.getString("constraintDelegationField")).thenReturn("constraintDelegation");
		command = (ParseKerberosTicketOptions) builder.build(config, sink, sink, morphlineContext);
		
		// perpare record
		Record record = getRecord(ticket);
		
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertValueBoolean(output, "forwardable", ("forwardable".equals(field)));
		assertValueBoolean(output, "forwarded", ("forwarded".equals(field)));
		assertValueBoolean(output, "proxied", ("proxied".equals(field)));
		assertValueBoolean(output, "postdated", ("postdated".equals(field)));
		assertValueBoolean(output, "renewRequest", ("renewRequest".equals(field)));
		assertValueBoolean(output, "constraintDelegation", ("constraintDelegation".equals(field)));
	}
	
	private void assertValueBoolean(Record record, String field, boolean status) {
		String outcome = record.getFirstValue(field).toString();
		if (status)
			assertTrue(outcome.equalsIgnoreCase("True"));
		else
			assertTrue(outcome.equalsIgnoreCase("False"));
	}
	
	
	private boolean isRecordHasField(Record record, String field) {
		return record.getFields().containsKey(field);
	}
	
}
