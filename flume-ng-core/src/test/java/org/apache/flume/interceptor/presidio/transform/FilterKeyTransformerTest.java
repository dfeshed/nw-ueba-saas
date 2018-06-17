package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class FilterKeyTransformerTest extends TransformerTest{

    private IJsonObjectTransformer buildTransformer(String keyToFilter) {
        return new FilterKeyTransformer("testName",keyToFilter);
    }


    @Test
    public void filter_key_test() throws JsonProcessingException {
        String keyToFilter = "key-to-filter";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(keyToFilter, "some value");

        IJsonObjectTransformer transformer = buildTransformer(keyToFilter);
        JSONObject retJsonObject = transform(transformer, jsonObject);

        Assert.assertTrue(String.format("The key '%s' should have been filtered. event: %s", keyToFilter, retJsonObject), !retJsonObject.has(keyToFilter));
    }
}
