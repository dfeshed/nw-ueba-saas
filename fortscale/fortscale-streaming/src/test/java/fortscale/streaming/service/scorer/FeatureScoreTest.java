package fortscale.streaming.service.scorer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.FeatureScore;
import fortscale.domain.core.FeatureScoreList;
import fortscale.domain.core.ModelFeatureScore;
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
        FeatureScore modelFeatureScore = new ModelFeatureScore("testModelName",85D,null,0.5);
        featureScores1.add(modelFeatureScore);
        FeatureScore featureScore = new FeatureScore("test",0.19500000000000017,featureScores1);
        List<FeatureScore> featureScores = new FeatureScoreList();
        featureScores.add(featureScore);

        ObjectMapper mapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", featureScores);
        String jsonObjectStr1 = mapper.writeValueAsString(jsonObject);//jsonObject.toJSONString();
//        Assert.assertEquals("{\"test\":[{\"score\":0.19500000000000017,\"featureScores\":[{\"score\":0.30000000000000027,\"featureScores\":[],\"name\":\"test1\"}],\"name\":\"test\"}]}",jsonObjectStr1);

        JSONObject message;

        try {
            message = (JSONObject) JSONValue.parseWithException(jsonObjectStr1);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse message: " + jsonObjectStr1);
        }



//        Assert.assertEquals(jsonObjectStr1,message.toJSONString());


        List<FeatureScore> desFeatureScores = mapper.readValue(message.getAsString("test"), new TypeReference<FeatureScoreList>(){});

        Assert.assertEquals(featureScores,desFeatureScores);

        message.put("addedKey","addedVal");
        String jsonObjectStr2 = message.toJSONString();
        try {
            message = (JSONObject) JSONValue.parseWithException(jsonObjectStr2);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse message: " + jsonObjectStr2);
        }

        desFeatureScores = mapper.readValue(message.getAsString("test"), new TypeReference<FeatureScoreList>(){});

        Assert.assertEquals(featureScores,desFeatureScores);
    }
}
