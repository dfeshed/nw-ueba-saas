package fortscale.common.s3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetwitnessS3EventExtractorTest {
    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void map_extractor_test() throws IOException {
        String expectedRecordStr = "{\"userdst\":\"SE-SYSTEM-4$\",\"usersrc\":\"dglover\",\"objname\":\"<empty>\",\"eventsourceid\":\"<empty>\",\"eventtime\":1081847839000,\"eventtype\":\"Success Audit\",\"sessionid\":86,\"accesses\":\"<empty>\",\"referenceid\":\"642\",\"devicetype\":\"winevent_nic\",\"group1\":\"<empty>\"}";
        String eventStr = "{\"records\":[" + expectedRecordStr + "]}";
        Map<String, Object> expectedRecord = MAPPER.readValue(expectedRecordStr, TYPE);

        NetwitnessS3EventExtractor netwitnessS3EventExtractor = new NetwitnessS3EventExtractor();
        Map<String, Object> record = netwitnessS3EventExtractor.extract(eventStr);

        Assert.assertEquals(expectedRecord, record);
    }
}
