package fortscale.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class S3DataIteratorTest {

    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void reading_records_test() throws IOException {
        String filePath = "s3/records.json.gz";
        //The records in this file
        String firstRecordStr = "{\"userdst\":\"SE-SYSTEM-4$\",\"usersrc\":\"dglover\",\"objname\":\"<empty>\",\"eventsourceid\":\"<empty>\",\"eventtime\":1081847839000,\"eventtype\":\"Success Audit\",\"sessionid\":86,\"accesses\":\"<empty>\",\"referenceid\":\"642\",\"devicetype\":\"winevent_nic\",\"group1\":\"<empty>\"}";
        Map<String, Object> firstRecord = MAPPER.readValue(firstRecordStr, TYPE);
        String secondRecordStr = "{\"userdst\":\"dglover\",\"usersrc\":\"<empty>\",\"objname\":\"<empty>\",\"eventsourceid\":\"<empty>\",\"eventtime\":1081847839000,\"eventtype\":\"Success Audit\",\"sessionid\":100,\"accesses\":\"<empty>\",\"referenceid\":\"644\",\"devicetype\":\"winevent_nic\",\"group1\":\"<empty>\"}";
        Map<String, Object> secondRecord = MAPPER.readValue(secondRecordStr, TYPE);

        String dummyBucket = "dummyBucket";

        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey(filePath);
        Iterator<S3ObjectSummary> s3ObjectSummaryIterator = Collections.singletonList(s3ObjectSummary).iterator();
        AmazonS3 s3 = createMockedAmazonS3WithInputStream(dummyBucket, filePath);
        S3DataIterator s3DataIterator = new S3DataIterator(s3, dummyBucket, s3ObjectSummaryIterator);
        assertNextRecord(s3DataIterator, firstRecord);
        assertNextRecord(s3DataIterator, secondRecord);
        Assert.assertFalse(s3DataIterator.hasNext());

    }

    private AmazonS3 createMockedAmazonS3WithInputStream(String dummyBucket, String filePath){
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        AmazonS3 s3 = mock(AmazonS3.class);
        S3Object s3Object = mock(S3Object.class);
        when(s3.getObject(dummyBucket,filePath)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(inputStream, null, false));
        return s3;
    }

    private void assertNextRecord(S3DataIterator s3DataIterator,Map<String, Object> expectedRecord){
        Assert.assertTrue(s3DataIterator.hasNext());
        Map<String, Object> record = s3DataIterator.next();
        Assert.assertNotNull(record);
        Assert.assertEquals(expectedRecord.size(), record.size());
        record.keySet().stream().forEach(k -> {Assert.assertEquals(expectedRecord.get(k),record.get(k));});
    }
}
