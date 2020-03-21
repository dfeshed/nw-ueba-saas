package fortscale.common.s3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapExtractorTest {
    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void map_extractor_test() throws IOException {
        String eventStr = "{\"records\":[{\"userdst\":\"SE-SYSTEM-4$\",\"usersrc\":\"dglover\",\"objname\":\"<empty>\",\"eventsourceid\":\"<empty>\",\"eventtime\":1081847839000,\"eventtype\":\"Success Audit\",\"sessionid\":86,\"accesses\":\"<empty>\",\"referenceid\":\"642\",\"devicetype\":\"winevent_nic\",\"group1\":\"<empty>\"}]}";
        String expectedRecordStr = "{\"userdst\":\"SE-SYSTEM-4$\",\"usersrc\":\"dglover\",\"objname\":\"<empty>\",\"eventsourceid\":\"<empty>\",\"eventtime\":1081847839000,\"eventtype\":\"Success Audit\",\"sessionid\":86,\"accesses\":\"<empty>\",\"referenceid\":\"642\",\"devicetype\":\"winevent_nic\",\"group1\":\"<empty>\"}";
        Map<String, Object> expectedRecord = MAPPER.readValue(expectedRecordStr, TYPE);

        MapExtractor mapExtractor = new MapExtractor();
        Map<String, Object> record = mapExtractor.extract(eventStr);

        Assert.assertNotNull(record);
        Assert.assertEquals(expectedRecord.size(), record.size());
        record.keySet().stream().forEach(k -> {Assert.assertEquals(expectedRecord.get(k),record.get(k));});
    }
}
