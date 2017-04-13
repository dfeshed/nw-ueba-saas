package fortscale.collection.morphlines;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.utils.logging.Logger;
import org.junit.Assert;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MorphlinesTester {

	protected MorphlinesItemsProcessor[] subjects;
	private List<String> outputFields;
	private static final Logger logger = Logger.getLogger(MorphlinesTester.class);

	private MorphlineMetrics morphlineMetrics;

	public MorphlinesTester() {

	}

	public void init(String[] confFiles, List<String> outputFields) {
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
		morphlineMetrics = new MorphlineMetrics(null, "dataSource");
	}

	public void close() throws IOException {
		for (MorphlinesItemsProcessor subject : subjects)
			if (subject!=null)
				subject.close();
	}

	public void testSingleLine(String testCase, String inputLine, String expectedOutput) {
		Record parsedRecord = new Record();
		parsedRecord.put(Fields.MESSAGE, inputLine);
		ItemContext itemContext = new ItemContext(null, null, morphlineMetrics);
		for (MorphlinesItemsProcessor subject : subjects) {
			if (parsedRecord!=null)
				parsedRecord = subject.process(parsedRecord, itemContext);
		}
		
		if (null == expectedOutput) {
			assertEquals("ETL error with " + testCase, null ,parsedRecord);
		}
		
		else {
			testRecordPostProcessing(testCase, expectedOutput, parsedRecord);
		}
	}

	public void testRecordPostProcessing(String testCase, String expectedOutput, Record parsedRecord) {
		assertNotNull("parsed record should not be null", parsedRecord);
		String parsedOutput = "";
		String separator = "";
		for (String field : outputFields) {
            String parsedField = (null == parsedRecord.getFirstValue(field)) ? "" : parsedRecord.getFirstValue(field).toString() ;
            parsedOutput += separator + parsedField;
            separator = ",";
        }
		assertEquals("ETL error with " + testCase, expectedOutput ,parsedOutput);
	}

	public void runMorphlines(String inputLine) {
		Record parsedRecord = new Record();
		parsedRecord.put(Fields.MESSAGE, inputLine);
		ItemContext itemContext = new ItemContext(null, null, morphlineMetrics);
		for (MorphlinesItemsProcessor subject : subjects) {
			if (parsedRecord!=null)
				parsedRecord = subject.process(parsedRecord, itemContext);
		}
	}

	
	public void testMultipleLines(String testCase, List<String> lines, List<String> expectedOutput) {
		assertNotNull("expected to receive multiple lines to process", lines);
		assertEquals(lines.size(), expectedOutput.size());
		
		// process each line 
		for (int i=0;i<lines.size(); i++) {
			String input = lines.get(i);
			String expected = expectedOutput.get(i);
			
			testSingleLine(testCase, input, expected);
		}
	}


	public void testSingleLineFiltered(String testCase, String inputLine) {
		Record parsedRecord = new Record();
		parsedRecord.put(Fields.MESSAGE, inputLine);
		ItemContext itemContext = new ItemContext(null, null, morphlineMetrics);
		for (MorphlinesItemsProcessor subject : subjects) {
			if (parsedRecord!=null)
				parsedRecord = subject.process(parsedRecord, itemContext);
		}

		Assert.assertNull(testCase, parsedRecord);
	}

}