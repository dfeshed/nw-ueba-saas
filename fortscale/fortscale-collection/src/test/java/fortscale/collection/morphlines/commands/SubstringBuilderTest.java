package fortscale.collection.morphlines.commands;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import fortscale.collection.morphlines.commands.SubstringBuilder.SubString;

@RunWith(JUnitParamsRunner.class)
public class SubstringBuilderTest {

	private RecordSinkCommand sink;
	private SubString command;
	private Config config;
	private MorphlineContext morphlineContext;
	
	@Before
	public void setUp() throws Exception {
		sink = new RecordSinkCommand();
		morphlineContext = new MorphlineContext.Builder().build();
		config = mock(Config.class);
		
	}
	
	@Test
	@Parameters
	public void testSubString(String fieldValue, Integer startIndex, Integer endIndex, String endChar, String expectedOutput) {
		when(config.getString("field")).thenReturn("field");
		if (startIndex!=null) {
			when(config.getInt("startIndex")).thenReturn(startIndex);
			when(config.hasPath("startIndex")).thenReturn(true);
		}
		if (endIndex!=null) {
			when(config.getInt("endIndex")).thenReturn(endIndex);
			when(config.hasPath("endIndex")).thenReturn(true);
		}
		if (endChar!=null) {
			when(config.getString("endCharacter")).thenReturn(endChar);
			when(config.hasPath("endCharacter")).thenReturn(true);
		}
		
		// build the command
		SubstringBuilder builder = new SubstringBuilder();
		SubString command = (SubString) builder.build(config, sink, sink, morphlineContext);
		
		// prepare record
		Record record = new Record();
		record.put("field", fieldValue);
		
		// execute the parse
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(expectedOutput, output.getFirstValue("field"));
	}
	
	public Object[] parametersForTestSubString() {
		return $(
				$( "myname", null, null, ".", "myname"),
				$( "myname.localhost", null, null, ".", "myname"),
				$( null, null, null, ".", null),
				$( "myname.local", 7, null, null, "local")
				);
	}
	
}
