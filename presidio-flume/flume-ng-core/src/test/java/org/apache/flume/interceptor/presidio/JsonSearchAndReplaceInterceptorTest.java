package org.apache.flume.interceptor.presidio;

import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.InterceptorBuilderFactory;
import org.apache.flume.interceptor.InterceptorType;
import org.apache.flume.tools.MockMonitorInitiator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JsonSearchAndReplaceInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_SEARCH_AND_REPLACE.toString());
    }



    @Test
    public void interceptMultipleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.FIELDS_CONF_NAME, "field1;field1;field2");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.SEARCH_PATTERNS_CONF_NAME, "\\\\s+;old;-");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.REPLACE_STRINGS_CONF_NAME, "_;new;_");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_MULTIPLE_KEY = "{\"field1\": \"old value\", \"field2\": \"some-value\"}";

        Event event = EventBuilder.withBody(EVENT_MULTIPLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_MULTIPLE_KEY, interceptValue);

        Assert.assertEquals("{\"field1\":\"new value\",\"field2\":\"some_value\"}", interceptValue);
    }


    private void interceptWithCondition(String regexCondition, String field2NewValue){
        String field1Name = "field1";
        String field2Name = "field2";
        Context ctx = new Context();
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.CONDITION_FIELD_CONF_NAME, field1Name);
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.REGEX_CONDITION_CONF_NAME, regexCondition);
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.FIELDS_CONF_NAME, field2Name);
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.SEARCH_PATTERNS_CONF_NAME, "^FILE_");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.REPLACE_STRINGS_CONF_NAME, "FOLDER_");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        String field1Value = "\\\\some\\\\folder\\\\";
        String field1KeyValue = JsonInterceptorUtil.buildKeyValue(field1Name, field1Value);
        String field2Value = "FILE_OPENED_FILE_FILE";
        String field2KeyValue = JsonInterceptorUtil.buildKeyValue(field2Name, field2Value);
        ArrayList<String> fields = new ArrayList<>();
        fields.add(field1KeyValue);
        fields.add(field2KeyValue);

        Event event = JsonInterceptorUtil.buildEvent(fields);

        event = interceptor.intercept(event);
        String eventBody = new String(event.getBody());

        Assert.assertTrue(String.format("The event doesn't contain the field %s. event: %s", field1Name, eventBody),eventBody.contains(field1Name));
        Assert.assertTrue(String.format("The event contains the wrong value for the field %s.  expected key value: %s; event: %s", field1Name, field1KeyValue, eventBody),eventBody.contains(field1KeyValue));
        Assert.assertTrue(String.format("The event doesn't contain the field %s. event: %s", field2Name, eventBody),eventBody.contains(field2Name));
        String field2NewKeyValue = JsonInterceptorUtil.buildKeyValue(field2Name, field2NewValue);
        Assert.assertTrue(String.format("The event contains the wrong value for the field %s.  expected key value: %s; event: %s", field2Name, field2NewKeyValue, eventBody),eventBody.contains(field2NewKeyValue));
    }

    @Test
    public void interceptWithConditionMatch(){
        interceptWithCondition(".*\\\\$", "FOLDER_OPENED_FILE_FILE");
    }

    @Test
    public void interceptWithConditionNoMatch(){
        interceptWithCondition(".*D$", "FILE_OPENED_FILE_FILE");
    }

}