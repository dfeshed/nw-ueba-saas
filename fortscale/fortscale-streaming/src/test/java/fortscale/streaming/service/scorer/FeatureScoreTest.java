package fortscale.streaming.service.scorer;


import fortscale.domain.core.FeatureScore;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class FeatureScoreTest {



    @Test
    public void testJsonSerialize(){
        FeatureScore featureScore = new FeatureScore("test",null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", featureScore);
        String actual = jsonObject.toJSONString();
        Assert.assertEquals("{\"test\":{\"score\":null,\"featureScores\":[],\"name\":\"test\"}}",actual);
    }
}
