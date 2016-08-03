package fortscale.collection.morphlines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import com.google.common.collect.ListMultimap;

/**
 * Created by rans on 11/01/15.
 */
public class MorphlinesParseVpnTester extends MorphlinesTester {
    ArrayList<String> expectedFields = new ArrayList<String>(
            Arrays.asList("date_time", "date_time_unix", "status"));
    private MorphlineMetrics morphlineMetrics ;

    public MorphlinesParseVpnTester() {
        morphlineMetrics= new MorphlineMetrics(null, "dataSource");
    }

    @Override
    public void testSingleLine(String testCase, String inputLine, String expectedOutput) {
        Record parsedRecord = new Record();
        parsedRecord.put(Fields.MESSAGE, inputLine);
        ItemContext itemContext = new ItemContext(null, null, morphlineMetrics);
        for (MorphlinesItemsProcessor subject : subjects) {
            if (parsedRecord!=null)
                parsedRecord = (Record) subject.process(parsedRecord,itemContext);
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
