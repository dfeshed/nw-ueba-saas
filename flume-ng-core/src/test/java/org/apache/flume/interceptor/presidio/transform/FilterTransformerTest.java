package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flume.interceptor.presidio.transform.predicate.JsonObjectRegexPredicate;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flume.interceptor.presidio.transform.TransformerUtil.assertNewJsonObjectNotContainsOriginalJsonObject;

public class FilterTransformerTest extends TransformerTest{

    @Test
    public void filter_in_predicate_true() throws JsonProcessingException {
        String sourceKey = "test-key";
        JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, "^condition.*");
        FilterTransformer filterTransformer = new FilterTransformer("test-name", predicate, true);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "condition-testing");

        JSONObject retJsonObject = transform(filterTransformer, jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
    }

    @Test
    public void filter_out_predicate_true() throws JsonProcessingException {
        String sourceKey = "test-key";
        JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, "^condition.*");
        FilterTransformer filterTransformer = new FilterTransformer("test-name", predicate, false);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "condition-testing");

        JSONObject retJsonObject = transform(filterTransformer, jsonObject, true);

        Assert.assertNull("the event was not filtered", retJsonObject);
    }

    @Test
    public void filter_out_predicate_false() throws JsonProcessingException {
        String sourceKey = "test-key";
        JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, "^condition.*");
        FilterTransformer filterTransformer = new FilterTransformer("test-name", predicate, false);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "testing-condition");

        JSONObject retJsonObject = transform(filterTransformer, jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
    }

    @Test
    public void filter_in_predicate_false() throws JsonProcessingException {
        String sourceKey = "test-key";
        JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, "^condition.*");
        FilterTransformer filterTransformer = new FilterTransformer("test-name", predicate, true);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "testing-condition");

        JSONObject retJsonObject = transform(filterTransformer, jsonObject, true);

        Assert.assertNull("the event was not filtered", retJsonObject);
    }
}
