package org.apache.flume.interceptor.presidio.conditionalarraypopulator;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor;
import org.apache.flume.interceptor.presidio.conditionalarraypopulator.JsonConditionalArrayPopulatorInterceptor.Builder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class JsonConditionalArrayPopulatorInterceptorTest {
    @Test
    public void test_when_destination_key_is_not_present_and_overwrite_array_flag_is_off() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(false,
                "{\"pattern\":\"COMPUTER_ACCOUNT_CREATED\",\"values\":[\"COMPUTER_MANAGEMENT\",\"OBJECT_MANAGEMENT\"]}"
        );

        Event event = buildEvent("COMPUTER_ACCOUNT_CREATED");
        event = interceptor.doIntercept(event);
        assertEvent(event, "COMPUTER_MANAGEMENT", "OBJECT_MANAGEMENT");
    }

    @Test
    public void test_when_destination_key_is_not_present_and_overwrite_array_flag_is_on() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(true,
                "{\"pattern\":\"COMPUTER_ACCOUNT_CHANGED\",\"values\":[\"COMPUTER_MANAGEMENT\",\"OBJECT_MANAGEMENT\"]}"
        );

        Event event = buildEvent("COMPUTER_ACCOUNT_CHANGED");
        event = interceptor.doIntercept(event);
        assertEvent(event, "COMPUTER_MANAGEMENT", "OBJECT_MANAGEMENT");
    }

    @Test
    public void test_when_destination_key_is_present_and_overwrite_array_flag_is_off() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(false,
                "{\"pattern\":\"USER_ACCOUNT_CREATED\",\"values\":[\"USER_MANAGEMENT\"]}"
        );

        Event event = buildEvent("USER_ACCOUNT_CREATED", "YET_ANOTHER_OPERATION_TYPE_CATEGORY");
        event = interceptor.doIntercept(event);
        assertEvent(event, "YET_ANOTHER_OPERATION_TYPE_CATEGORY", "USER_MANAGEMENT");
    }

    @Test
    public void test_when_destination_key_is_present_and_overwrite_array_flag_is_on() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(true,
                "{\"pattern\":\"USER_ACCOUNT_DELETED\",\"values\":[\"USER_MANAGEMENT\"]}"
        );

        Event event = buildEvent("USER_ACCOUNT_DELETED", "YET_ANOTHER_OPERATION_TYPE_CATEGORY");
        event = interceptor.doIntercept(event);
        assertEvent(event, "USER_MANAGEMENT");
    }

    @Test
    public void test_when_source_key_is_not_present_and_destination_key_is_not_present() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(false,
                "{\"pattern\":\"USER_ACCOUNT_CHANGED\",\"values\":[\"USER_MANAGEMENT\"]}"
        );

        Event event = buildEvent(null);
        event = interceptor.doIntercept(event);
        assertEvent(event);
    }

    @Test
    public void test_when_source_key_is_not_present_and_destination_key_is_present() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(false,
                "{\"pattern\":\"USER_ACCOUNT_CHANGED\",\"values\":[\"USER_MANAGEMENT\"]}"
        );

        Event event = buildEvent(null, "YET_ANOTHER_OPERATION_TYPE_CATEGORY");
        event = interceptor.doIntercept(event);
        assertEvent(event, "YET_ANOTHER_OPERATION_TYPE_CATEGORY");
    }

    @Test
    public void test_when_the_pattern_is_a_regular_expression() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(false,
                "{\"pattern\":\".*(?i:allison).*\",\"values\":[\"YET_ANOTHER_OPERATION_TYPE_CATEGORY\"]}"
        );

        Event event = buildEvent("yesAllIsOnIndeed");
        event = interceptor.doIntercept(event);
        assertEvent(event, "YET_ANOTHER_OPERATION_TYPE_CATEGORY");
    }

    private static AbstractPresidioJsonInterceptor buildInterceptor(boolean overwriteArray, String... conditionAndArrayValuesList) {
        String configuration = String.format(
                "{\"sourceKey\":\"operationType\",\"destinationKey\":\"operationTypeCategories\"," +
                "\"overwriteArray\":%s,\"conditionAndArrayValuesList\":[%s]}",
                overwriteArray ? "true" : "false", String.join(",", conditionAndArrayValuesList));
        Context context = mock(Context.class);
        when(context.getString(eq(Builder.CONFIGURATION_KEY))).thenReturn(configuration);
        Builder builder = new Builder();
        builder.doConfigure(context);
        return builder.doBuild();
    }

    private static Event buildEvent(String sourceValue, String... destinationValues) {
        JSONObject jsonObject = new JSONObject();

        // If the source value is null, the source key should not exist at all
        if (sourceValue != null) {
            jsonObject.put("operationType", sourceValue);
        }

        // If there are no destination values, the destination key should not exist at all
        if (destinationValues.length > 0) {
            JSONArray jsonArray = new JSONArray();
            for (String destinationValue : destinationValues) jsonArray.put(destinationValue);
            jsonObject.put("operationTypeCategories", jsonArray);
        }

        Event event = new JSONEvent();
        event.setBody(jsonObject.toString().getBytes());
        return event;
    }

    private static void assertEvent(Event event, String... destinationValues) {
        String body = new String(event.getBody());
        JSONObject jsonObject = new JSONObject(body);

        if (destinationValues.length == 0) {
            Assert.assertFalse(jsonObject.has("operationTypeCategories"));
        } else {
            JSONArray jsonArray = jsonObject.getJSONArray("operationTypeCategories");
            Assert.assertEquals(destinationValues.length, jsonArray.length());

            for (int i = 0; i < destinationValues.length; i++) {
                Assert.assertEquals(destinationValues[i], jsonArray.get(i));
            }
        }
    }
}
