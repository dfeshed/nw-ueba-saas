package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by YaronDL on 1/11/2018.
 */
public class ConstantScorerConfTest {

    @Test
    public void should_deserialize_conditional_scorer_conf_from_json() throws IOException {
        String name = "myScorer";
        Double constantScore = 50.0;

        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, name, constantScore);
        IScorerConf scorerConf = getScorerConf(jsonObject);

        Assert.assertEquals(ConstantScorerConf.class, scorerConf.getClass());
        Assert.assertEquals(name, scorerConf.getName());
        Assert.assertEquals(constantScore, ((ConstantScorerConf)scorerConf).getConstantScore());
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_null() throws IOException {
        Double constantScore = 50.0;
        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, null, constantScore);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_empty() throws IOException {
        Double constantScore = 50.0;
        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, "", constantScore);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_blank() throws IOException {
        Double constantScore = 50.0;
        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, "   ", constantScore);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_constant_score_is_null() throws IOException {
        String name = "myConstantScorer";
        Double constantScore = null;
        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, name, constantScore);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_constant_score_is_below_zero() throws IOException {
        String name = "myConstantScorer";
        Double constantScore = -1.0;
        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, name, constantScore);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_constant_score_is_above_100() throws IOException {
        String name = "myConstantScorer";
        Double constantScore = 100.1;
        JSONObject jsonObject = getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, name, constantScore);
        getScorerConf(jsonObject);
    }

    public static JSONObject getConstantScorerConfJsonObject(String type, String name, Double constantScore) {

        JSONObject jsonObject = new JSONObject();
        if (type != null) jsonObject.put("type", type);
        if (name != null) jsonObject.put("name", name);
        if (constantScore != null) jsonObject.put("constant-score", constantScore);
        return jsonObject;
    }

    public static IScorerConf getScorerConf(JSONObject jsonObject) throws IOException {
        return new ObjectMapper().readValue(jsonObject.toJSONString(), IScorerConf.class);
    }
}
