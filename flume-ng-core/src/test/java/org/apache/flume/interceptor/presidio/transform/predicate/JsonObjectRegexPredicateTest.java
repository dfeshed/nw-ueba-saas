package org.apache.flume.interceptor.presidio.transform.predicate;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonObjectRegexPredicateTest {

    @Test
    public void match_pattern_test(){
        String sourceKey = "test-key";
        JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, "^condition.*");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "condition-testing");

        Assert.assertTrue(String.format("The pattern %s should have matched the value '%s'",predicate.getRegex(), jsonObject.getString(sourceKey)), predicate.test(jsonObject));
    }

    @Test
    public void not_match_pattern_test(){
        String sourceKey = "test-key";
        JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, ".*condition$");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "condition-testing");

        Assert.assertFalse(String.format("The pattern %s should not have matched the value '%s'",predicate.getRegex(), jsonObject.getString(sourceKey)), predicate.test(jsonObject));
    }
}
