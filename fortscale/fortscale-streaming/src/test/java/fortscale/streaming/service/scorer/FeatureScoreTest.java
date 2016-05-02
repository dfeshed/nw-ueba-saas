package fortscale.streaming.service.scorer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.FeatureScore;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureScoreTest {



    @Test
    public void testJsonSerialize() throws IOException {
        FeatureScore featureScore1 = new FeatureScore("test1",0.30000000000000027);
        List<FeatureScore> featureScores1 = new ArrayList<>();
        featureScores1.add(featureScore1);
        FeatureScore featureScore = new FeatureScore("test",0.19500000000000017,featureScores1);
        List<FeatureScore> featureScores = new ArrayList<>();
        featureScores.add(featureScore);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", featureScores);
        String actual = jsonObject.toJSONString();
        Assert.assertEquals("{\"test\":[{\"score\":0.19500000000000017,\"featureScores\":[{\"score\":0.30000000000000027,\"featureScores\":[],\"name\":\"test1\"}],\"name\":\"test\"}]}",actual);

        JSONObject message;

        try {
            message = (JSONObject) JSONValue.parseWithException(actual);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse message: " + actual);
        }

        Assert.assertEquals(actual,message.toJSONString());

        ObjectMapper mapper = new ObjectMapper();
        List<FeatureScore> desFeatureScores = mapper.readValue(message.getAsString("test"), new TypeReference<List<FeatureScore>>(){});

        Assert.assertEquals(featureScores,desFeatureScores);
    }
}
