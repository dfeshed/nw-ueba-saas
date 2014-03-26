package fortscale.collection.morphlines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import fortscale.utils.logging.Logger;

public class MorphlinesTester {

	private MorphlinesItemsProcessor[] subjects;
	private List<String> outputFields;
	private static final Logger logger = Logger.getLogger(MorphlinesTester.class);
	
	public MorphlinesTester() {
	}

	public void init(String[] confFiles,List<String> outputFields) {
		try {
			subjects = new MorphlinesItemsProcessor[confFiles.length];
			for (int i=0;i<confFiles.length;i++) {
				Resource conf = new FileSystemResource(confFiles[i]);
				subjects[i] = new MorphlinesItemsProcessor(conf);
			}
			
			this.outputFields = outputFields;
		}
		catch (IOException e) {
			logger.error("Exception while initializing morphline test class",e);
		}
	}
	
	public void init(String confFile,List<String> outputFields) {
		init(new String[] { confFile }, outputFields);
	}
	
	public void close() throws IOException {
		for (MorphlinesItemsProcessor subject : subjects)
			subject.close();
	}

	public void testSingleLine(String testCase, String inputLine, String expectedOutput) {
		Record parsedRecord = new Record();
		parsedRecord.put(Fields.MESSAGE, inputLine);
		for (MorphlinesItemsProcessor subject : subjects) {
			if (parsedRecord!=null)
				parsedRecord = (Record) subject.process(parsedRecord);
		}
		
		if (null == expectedOutput) {
			assertEquals("ETL error with " + testCase, null ,parsedRecord);
		}
		
		else {
			assertNotNull("parsed record should not be null", parsedRecord);
			String parsedOutput = "";
			String seperator = "";
			for (String field : outputFields) {
				String parsedField = (null == parsedRecord.getFirstValue(field)) ? "" : parsedRecord.getFirstValue(field).toString() ;
				parsedOutput += seperator + parsedField;
				seperator = ",";
			}
			assertEquals("ETL error with " + testCase, expectedOutput ,parsedOutput);
		}
	}
	
	public void testMultipleLines(String testCase, List<String> lines, List<String> expectedOutput) {
		assertNotNull("expected to recieve multiple lines to process", lines);		
		assertEquals(lines.size(), expectedOutput.size());
		
		// process each line 
		for (int i=0;i<lines.size(); i++) {
			String input = lines.get(i);
			String expected = expectedOutput.get(i);
			
			testSingleLine(testCase, input, expected);
		}
	}

}