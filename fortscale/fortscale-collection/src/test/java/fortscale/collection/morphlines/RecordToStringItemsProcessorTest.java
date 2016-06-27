package fortscale.collection.morphlines;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kitesdk.morphline.api.Record;

public class RecordToStringItemsProcessorTest {

	@Test(expected=IllegalArgumentException.class)
	public void ctor_should_fail_on_null_arguments() throws IllegalArgumentException {
		new RecordToStringItemsProcessor(null,null,"TEST-NO-VALUE");
	}
	
	@Test
	public void process_should_return_null_when_record_is_null() {
		// arrange
		RecordToStringItemsProcessor subject = new RecordToStringItemsProcessor(",",null,"TEST-NO-VALUE", "fieldA", "fieldB", "fieldC");
		
		// act
		String output = subject.process(null);
		
		// assert
		assertNull(output);
	}
	
	@Test
	public void process_should_return_null_when_fields_does_not_exists_in_record() {
		// arrange
		RecordToStringItemsProcessor subject = new RecordToStringItemsProcessor(",",null,"TEST-NO-VALUE", "fieldA", "fieldB", "fieldC");
		Record record = new Record();
		
		// act
		String output = subject.process(record);
		
		// assert
		assertNull(output);
	}
	
	@Test
	public void process_should_return_all_fields_that_are_specified_including_null() {
		// arrange
		RecordToStringItemsProcessor subject = new RecordToStringItemsProcessor(",",null,"TEST-NO-VALUE", "fieldA", "fieldB", "fieldC");
		Record record = new Record();
		record.put("fieldA", "AAA");
		record.put("fieldC", "CCC");
		
		// act
		String output = subject.process(record);
		
		// assert
		assertEquals("AAA,,CCC", output);
	}
		
	@Test
	public void process_should_join_with_separator() {
		// arrange
		RecordToStringItemsProcessor subject = new RecordToStringItemsProcessor(";",null,"TEST-NO-VALUE", "fieldA", "fieldB");
		Record record = new Record();
		record.put("fieldA", "AAA");
		record.put("fieldB", "BBB");
		
		// act
		String output = subject.process(record);
		
		// assert
		assertEquals("AAA;BBB", output);
	}
	
	@Test
	public void process_of_one_item_should_not_have_separator_in_output() {
		// arrange
		RecordToStringItemsProcessor subject = new RecordToStringItemsProcessor(";",null,"TEST-NO-VALUE", "fieldA");
		Record record = new Record();
		record.put("fieldA", "AAA");
		
		// act
		String output = subject.process(record);
		
		// assert
		assertEquals("AAA", output);
	}
	
	@Test
	public void tojson_should_output_all_fields() {
		// arrange
		RecordToStringItemsProcessor subject = new RecordToStringItemsProcessor(";",null,"TEST-NO-VALUE", "fieldA", "fieldB", "fieldC", "fieldD");
		Record record = new Record();
		record.put("fieldA", "AAA");
		record.put("fieldB", "BBB");
		record.put("fieldC", 5000000000L);
		record.put("fieldD", true);
		
		// act
		String output = subject.toJSON(record);
		
		// assert
		assertEquals("{\"fieldA\":\"AAA\",\"fieldC\":5000000000,\"fieldB\":\"BBB\",\"fieldD\":true}", output);
	}

}
