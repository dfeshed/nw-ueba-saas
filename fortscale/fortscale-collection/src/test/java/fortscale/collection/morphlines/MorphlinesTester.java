package fortscale.collection.morphlines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;

import org.junit.Test;
import org.kitesdk.morphline.api.Record;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import fortscale.utils.logging.Logger;

public class MorphlinesTester {

	private MorphlinesItemsProcessor subject;
	private String[] outputFields;
	private static final Logger logger = Logger.getLogger(MorphlinesTester.class);
	
	public MorphlinesTester() {
	}

	public void init(String confFile, String[] outputFields) {
		try {
			Resource conf = new FileSystemResource(confFile);
			subject = new MorphlinesItemsProcessor(conf);
			
			this.outputFields = outputFields;
		}
		catch (IOException e) {
			logger.error("Exception while initializing morphline test class",e);
		}
	}

	@Test
	public void testSingleLine(String testCase, String inputLine, String expectedOutput) {
		Record parsedRecord = (Record) subject.process(inputLine);
		
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

}