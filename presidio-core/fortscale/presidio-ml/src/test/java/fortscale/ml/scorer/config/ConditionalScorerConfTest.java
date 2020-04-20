package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Test;
import presidio.ade.domain.record.predicate.AdeRecordReaderPredicate;
import presidio.ade.domain.record.predicate.BooleanAdeRecordReaderPredicate;
import presidio.ade.domain.record.predicate.ContainedInListAdeRecordReaderPredicate;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class ConditionalScorerConfTest {
    public static final JSONObject defaultScorerConfJsonObject = new JSONObject();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        defaultScorerConfJsonObject.put("type", ConstantRegexScorerConf.SCORER_TYPE);
        defaultScorerConfJsonObject.put("name", "myConstantRegexScorer");
        defaultScorerConfJsonObject.put("regex", "42");
        defaultScorerConfJsonObject.put("regex-field-name", "myRegexField");
        defaultScorerConfJsonObject.put("constant-score", 100);
    }

    @Test
    public void should_deserialize_conditional_scorer_conf() throws IOException {
        List<JSONObject> predicates = asList(getBooleanPredicateJsonObject("myBooleanField", true), getContainedInListPredicateJsonObject("myListField", singletonList("USER_MANAGEMENT"), "&"));
        JSONObject jsonObject = getConditionalScorerConfJsonObject("myConditionalScorer", predicates, defaultScorerConfJsonObject);
        IScorerConf scorerConf = getScorerConf(jsonObject);
        assertEquals(ConditionalScorerConf.class, scorerConf.getClass());
        ConditionalScorerConf conditionalScorerConf = (ConditionalScorerConf)scorerConf;
        assertEquals("myConditionalScorer", conditionalScorerConf.getName());
        assertEquals(2, conditionalScorerConf.getPredicates().size());
        assertEquals(getPredicate(getBooleanPredicateJsonObject("myBooleanField", true)), conditionalScorerConf.getPredicates().get(0));
        assertEquals(getPredicate(getContainedInListPredicateJsonObject("myListField", singletonList("USER_MANAGEMENT"), "&&")), conditionalScorerConf.getPredicates().get(1));
        assertEquals(getScorerConf(defaultScorerConfJsonObject), conditionalScorerConf.getScorerConf());
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_null() throws IOException {
        List<JSONObject> predicates = asList(getBooleanPredicateJsonObject("myBooleanField", false), getContainedInListPredicateJsonObject("myListField", singletonList("USER_MANAGEMENT"), "and"));
        JSONObject jsonObject = getConditionalScorerConfJsonObject(null, predicates, defaultScorerConfJsonObject);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_empty() throws IOException {
        List<JSONObject> predicates = singletonList(getBooleanPredicateJsonObject("myBooleanField", true));
        JSONObject jsonObject = getConditionalScorerConfJsonObject("", predicates, defaultScorerConfJsonObject);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_name_is_blank() throws IOException {
        List<JSONObject> predicates = singletonList(getContainedInListPredicateJsonObject("myListField", singletonList("USER_MANAGEMENT"), "or"));
        JSONObject jsonObject = getConditionalScorerConfJsonObject("   ", predicates, defaultScorerConfJsonObject);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_predicates_is_null() throws IOException {
        JSONObject jsonObject = getConditionalScorerConfJsonObject("myConditionalScorer", null, defaultScorerConfJsonObject);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_predicates_is_empty() throws IOException {
        JSONObject jsonObject = getConditionalScorerConfJsonObject("myConditionalScorer", emptyList(), defaultScorerConfJsonObject);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_predicates_contains_null_elements() throws IOException {
        JSONObject jsonObject = getConditionalScorerConfJsonObject("myConditionalScorer", singletonList(null), defaultScorerConfJsonObject);
        getScorerConf(jsonObject);
    }

    @Test(expected = JsonMappingException.class)
    public void should_fail_when_scorer_conf_is_null() throws IOException {
        List<JSONObject> predicates = asList(getBooleanPredicateJsonObject("myBooleanField", false), getContainedInListPredicateJsonObject("myListField", singletonList("USER_MANAGEMENT"), null));
        JSONObject jsonObject = getConditionalScorerConfJsonObject("myConditionalScorer", predicates, null);
        getScorerConf(jsonObject);
    }

    public static JSONObject getBooleanPredicateJsonObject(String fieldName, Boolean expectedValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", BooleanAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE);
        jsonObject.put("fieldName", fieldName);
        jsonObject.put("expectedValue", expectedValue);
        return jsonObject;
    }

    public static JSONObject getContainedInListPredicateJsonObject(String fieldName, List<String> expectedValues, String operator) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ContainedInListAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE);
        jsonObject.put("fieldName", fieldName);
        jsonObject.put("expectedValues", expectedValues);
        jsonObject.put("operator", operator);
        return jsonObject;
    }

    public static AdeRecordReaderPredicate getPredicate(JSONObject jsonObject) throws IOException {
        return objectMapper.readValue(jsonObject.toJSONString(), AdeRecordReaderPredicate.class);
    }

    public static JSONObject getConditionalScorerConfJsonObject(String name, List<JSONObject> predicates, JSONObject scorerConf) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ConditionalScorerConf.SCORER_TYPE);
        jsonObject.put("name", name);
        jsonObject.put("predicates", predicates);
        jsonObject.put("scorerConf", scorerConf);
        return jsonObject;
    }

    public static IScorerConf getScorerConf(JSONObject jsonObject) throws IOException {
        return objectMapper.readValue(jsonObject.toJSONString(), IScorerConf.class);
    }
}
