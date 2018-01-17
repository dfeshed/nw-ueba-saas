package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by YaronDL on 8/6/2017.
 */
public class ListConditionalScorerConfTest {
    public static final JSONObject defaultScorerConfJsonObject;

    static {
        defaultScorerConfJsonObject = new JSONObject();
        defaultScorerConfJsonObject.put("type", ConstantRegexScorerConf.SCORER_TYPE);
        defaultScorerConfJsonObject.put("name", "myConstantRegexScorer");
        defaultScorerConfJsonObject.put("regex", "42");
        defaultScorerConfJsonObject.put("regex-field-name", "myRegexField");
        defaultScorerConfJsonObject.put("constant-score", 100);
    }

    @Test
    public void should_deserialize_conditional_scorer_conf_from_json() throws IOException {
        String name = "mySubScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";

        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        IScorerConf scorerConf = getScorerConf(jsonObject);

        Assert.assertEquals(ListConditionalScorerConf.class, scorerConf.getClass());
        Assert.assertEquals(name, scorerConf.getName());
        Assert.assertEquals(getScorerConf(defaultScorerConfJsonObject), ((ListConditionalScorerConf)scorerConf).getScorer());
        Assert.assertEquals(conditionalField, ((ListConditionalScorerConf)scorerConf).getConditionalField());
        Assert.assertEquals(conditionalValue, ((ListConditionalScorerConf)scorerConf).getConditionalValue().get(0));
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_null() throws IOException {
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(ListConditionalScorerConf.SCORER_TYPE, null, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_empty() throws IOException {
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, "", defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_blank() throws IOException {
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(ListConditionalScorerConf.SCORER_TYPE, "   ", defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_scorer_conf_is_null() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(ListConditionalScorerConf.SCORER_TYPE, name, null, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_conditional_field_is_null() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = null;
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_conditional_field_is_empty() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = "";
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_conditional_field_is_blank() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = "   ";
        String conditionalValue = "myConditionalValue";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }







    @Test(expected = JsonMappingException.class)
    public void should_fail_when_conditional_value_is_null() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = null;
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_conditional_value_is_empty() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = "";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_conditional_value_is_blank() throws IOException {
        String name = "myConditionalScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = "   ";
        JSONObject jsonObject = getConditionalScorerConfJsonObject(
                ListConditionalScorerConf.SCORER_TYPE, name, defaultScorerConfJsonObject, conditionalField, conditionalValue);
        getScorerConf(jsonObject);
    }



    public static JSONObject getConditionalScorerConfJsonObject(String type, String name, JSONObject scorer, String conditionalField, String conditionalValue) {

        JSONObject jsonObject = new JSONObject();
        if (type != null) jsonObject.put("type", type);
        if (name != null) jsonObject.put("name", name);
        if (scorer != null) jsonObject.put("scorer", scorer);
        if (conditionalField != null) jsonObject.put("conditional-field", conditionalField);
        if (conditionalValue != null) jsonObject.put("conditional-value", Lists.newArrayList(conditionalValue));
        return jsonObject;
    }

    public static IScorerConf getScorerConf(JSONObject jsonObject) throws IOException {
        return new ObjectMapper().readValue(jsonObject.toJSONString(), IScorerConf.class);
    }
}
