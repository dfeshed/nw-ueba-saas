package fortscale.utils.kafka;

import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;



public class EqualityMetricsDeciderTest {


    @Test
    public void testJsonMappingWithEqualityToNumber(){
        String message = "{\"aggregation-events-streaming-last-message-epochtime\":1435108117,\"aggregation-message-count\":500000}";
        JSONObject messageJson = new JSONObject(message);
        EqualityMetricsDecider decider = new EqualityMetricsDecider();
        Map<String, Object> keyToExpectedValueMap = new HashMap<>();
        long latestEpochTimeSent = 1435108117;
        keyToExpectedValueMap.put("aggregation-events-streaming-last-message-epochtime", latestEpochTimeSent);
        decider.updateParams(keyToExpectedValueMap);

        boolean decision = decider.decide(messageJson);
        assertEquals(true,decision);
    }
}
