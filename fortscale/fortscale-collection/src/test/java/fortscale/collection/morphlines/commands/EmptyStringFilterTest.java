package fortscale.collection.morphlines.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.EmptyStringFilterBuilder.EmptyStringFilter;

public class EmptyStringFilterTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;
	private ConfigObject configRoot;


	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		configRoot = mock(ConfigObject.class);
		
	}
	
	private EmptyStringFilter getCommand(List<String> filterFields) {
		EmptyStringFilterBuilder builder = new EmptyStringFilterBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		when(config.getStringList("filterFields")).thenReturn(filterFields);
		when(config.root()).thenReturn(configRoot);
		when(configRoot.render()).thenReturn(filterFields.toString());
		return (EmptyStringFilter) builder.build(config, sink, sink, morphlineContext);
	}

	@Test
	public void testSingleFieldWithNullValueIsDropped(){
		List<String> filterFields = Arrays.asList("field1");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNull(output);
	}
	
	@Test
	public void testSingleFieldWithEmptyStringIsDropped(){
		List<String> filterFields = Arrays.asList("field1");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", "");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNull(output);
	}
	
	@Test
	public void testSingleFieldWithBlankStringIsDropped(){
		List<String> filterFields = Arrays.asList("field1");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", "   ");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNull(output);
	}
	
	@Test
	public void testSingleFieldWithNotEmptyStringIsNotDropped(){
		List<String> filterFields = Arrays.asList("field1");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", " val ");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNotNull(output);
	}
	
	@Test
	public void testMultiFieldWithOneNullValueIsDropped(){
		List<String> filterFields = Arrays.asList("field1","field2","field3");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", " val1 ");
		record.put("field3", " val3 ");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNull(output);
	}
	
	@Test
	public void testMultiFieldWithEmptyStringIsDropped(){
		List<String> filterFields = Arrays.asList("field1","field2","field3");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", "");
		record.put("field2", "");
		record.put("field3", "");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNull(output);
	}
	
	@Test
	public void testMultiFieldWithBlankStringIsDropped(){
		List<String> filterFields = Arrays.asList("field1","field2","field3");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", "   ");
		record.put("field2", "   ");
		record.put("field3", "   ");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNull(output);
	}
	
	@Test
	public void testMultiFieldWithNotEmptyStringIsNotDropped(){
		List<String> filterFields = Arrays.asList("field1","field2","field3");
		
		EmptyStringFilter command = getCommand(filterFields);
		
		Record record = new Record();
		record.put("field1", " val ");
		record.put("field2", " val2");
		record.put("field3", "val3");
		command.doProcess(record);
		Record output = sink.popRecord();
		assertNotNull(output);
	}
}
