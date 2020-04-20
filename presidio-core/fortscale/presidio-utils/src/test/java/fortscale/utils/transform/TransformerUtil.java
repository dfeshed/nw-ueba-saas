package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransformerUtil {



    public static JSONObject transform(ObjectMapper mapper, IJsonObjectTransformer transformer, JSONObject jsonObject, boolean isFilteredOut) throws JsonProcessingException {
        String transformerJsonAsString = mapper.writeValueAsString(transformer);

        return transform(mapper, transformerJsonAsString, jsonObject, isFilteredOut);
    }

    public static JSONObject transform(ObjectMapper mapper, IJsonObjectTransformer transformer, JSONObject jsonObject) throws JsonProcessingException {
        return transform(mapper, transformer, jsonObject, false);
    }

    public static JSONObject transform(ObjectMapper mapper, String transformerJsonAsString, JSONObject jsonObject) {
        return transform(mapper, transformerJsonAsString, jsonObject, false);
    }

    public static JSONObject transform(ObjectMapper mapper, String transformerJsonAsString, JSONObject jsonObject, boolean isFilteredOut) {
        IJsonObjectTransformer transformer = null;
        try {
            transformer = mapper.readValue(transformerJsonAsString, IJsonObjectTransformer.class);
        } catch (IOException e) {
            Assert.fail(String.format("Could not deserialize to transformer the following string %s", transformerJsonAsString));
        }

        JSONObject retJsonObject = transformer.transform(new JSONObject(jsonObject.toString()));
        if(!isFilteredOut) {
            Assert.assertNotNull(String.format("failed transformer: %s",transformer.getName()),retJsonObject);
        } else {
            Assert.assertNull("The event was expected to be filtered out.",retJsonObject);
        }

        return retJsonObject;
    }



    public static void assertJsonObjectValueRemovedOrModified(JSONObject jsonObject, String key, Object val){
        Assert.assertTrue(String.format("The following key has been removed. key value: %s, event: %s",
                key, jsonObject),jsonObject.has(key));
        Assert.assertTrue(String.format("The following key value has been modified. key value: %s, event: %s",
                val, jsonObject),jsonObject.get(key).equals(val));
    }

    public static void assertJsonObjectKeyNotAdded(JSONObject jsonObject, String key){
        Assert.assertTrue(String.format("The following key has not been added. key value: %s, event: %s",
                key, jsonObject),jsonObject.has(key));
    }

    public static void assertWrongValueAddedToKey(JSONObject jsonObject, String key, Object expectedValue){
        Assert.assertTrue(String.format("The wrong value has been inserted to the key %s. expected value: %s, actual value: %s event: %s",
                key, expectedValue, jsonObject.get(key), jsonObject),
                jsonObject.get(key).equals(expectedValue));
    }

    public static void assertNewJsonObjectNotContainsOriginalJsonObject(JSONObject newJsonObject, JSONObject origJsonObject){
        for(Object key: origJsonObject.keySet()){
            assertJsonObjectValueRemovedOrModified(newJsonObject, (String)key, origJsonObject.get((String)key));
        }
    }
}
