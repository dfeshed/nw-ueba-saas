package fortscale.collection.morphlines;

import com.google.common.collect.ListMultimap;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by rans on 11/01/15.
 */
public class MorphlinesParseVpnTester extends MorphlinesTester {
    ArrayList<String> expectedFields = new ArrayList<String>(
            Arrays.asList("date_time", "date_time_unix", "status"));


    @Override
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
            //Test that the field "status" contains the proper value.
            assertEquals("Status value is not correct", expectedOutput, RecordExtensions.getStringValue(parsedRecord, "status"));
            ListMultimap<String, Object> fields = parsedRecord.getFields();
            for (String field : expectedFields) {
                assertTrue("Mandatory value is missing in record: " + field, fields.containsKey(field));
            }
        }
    }
}
