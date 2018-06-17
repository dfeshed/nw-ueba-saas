package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor;
import org.apache.flume.interceptor.presidio.JsonInterceptorUtil;
import org.apache.flume.tools.MockMonitorInitiator;
import org.json.JSONObject;
import org.junit.Assert;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransformerUtil {

    public static AbstractPresidioJsonInterceptor buildInterceptor(String configuration) {
        Context context = mock(Context.class);
        when(context.getString(eq(TransformerInterceptor.Builder.CONFIGURATION_KEY),any())).thenReturn(configuration);
        return buildInterceptor(context);
    }

    public static AbstractPresidioJsonInterceptor buildInterceptor(Context context) {
        TransformerInterceptor.Builder builder = new TransformerInterceptor.Builder();
        builder.doConfigure(context);
        AbstractPresidioJsonInterceptor interceptor = builder.doBuild();
        MockMonitorInitiator.setMockMonitor(interceptor);
        return interceptor;
    }

    public static JSONObject transform(ObjectMapper mapper, IJsonObjectTransformer transformer, JSONObject jsonObject, boolean isFilteredOut) throws JsonProcessingException {
        String transformerJsonAsString = mapper.writeValueAsString(transformer);

        return transform(transformerJsonAsString, jsonObject, isFilteredOut);
    }

    public static JSONObject transform(ObjectMapper mapper, IJsonObjectTransformer transformer, JSONObject jsonObject) throws JsonProcessingException {
        return transform(mapper, transformer, jsonObject, false);
    }

    public static JSONObject transform(String transformerJsonAsString, JSONObject jsonObject) {
        return transform(transformerJsonAsString, jsonObject, false);
    }

    public static JSONObject transform(String transformerJsonAsString, JSONObject jsonObject, boolean isFilteredOut) {
        Interceptor interceptor = buildInterceptor(transformerJsonAsString);

        Event event = JsonInterceptorUtil.buildEvent(jsonObject);

        event = interceptor.intercept(event);
        if(!isFilteredOut) {
            Assert.assertNotNull(event);

            JSONObject retJsonObject = new JSONObject(new String(event.getBody()));

            return retJsonObject;
        } else {
            Assert.assertNull("The event was expected to be filtered out.",event);
            return null;
        }
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
