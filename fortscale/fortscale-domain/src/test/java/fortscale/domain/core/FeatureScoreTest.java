package fortscale.domain.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class FeatureScoreTest {



    @Test
    public void testJsonSerialize() throws JsonProcessingException {
        FeatureScore featureScore = new FeatureScore("test",null);
        ObjectMapper mapper = new ObjectMapper();
        String actual = mapper.writeValueAsString(featureScore);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("test", featureScore);
//        String test = jsonObject.toJSONString();
        Assert.assertEquals("{\"name\":\"test\",\"score\":null,\"featureScores\":[]}",actual);
    }
}
