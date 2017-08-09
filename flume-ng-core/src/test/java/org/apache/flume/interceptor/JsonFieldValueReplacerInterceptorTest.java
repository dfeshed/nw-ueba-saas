package org.apache.flume.interceptor;

import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by tomerd on 8/8/2017.
 */
public class JsonFieldValueReplacerInterceptorTest {

    Interceptor.Builder builder;
    Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_FIELD_VALUE_REPLACER.toString());
    }

    @Test
    public void interceptSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.REPLACEMENTS_CONF_NAME, "orig1#old value>new value;orig2#old value>new value");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.FIELD_DELIMITER_CONF_NAME, "#");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.VALUES_DELIMITER_CONF_NAME, ">");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"orig1\":\"old value\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String replacedKeys = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, replacedKeys);


        Assert.assertEquals("{\"orig1\":\"new value\"}", replacedKeys);
    }

    @Test
    public void interceptDoubleKey() throws Exception {
        Context ctx = new Context();
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.REPLACEMENTS_CONF_NAME, "orig1#old value>new value;orig2#old value>new value");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.FIELD_DELIMITER_CONF_NAME, "#");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.VALUES_DELIMITER_CONF_NAME, ">");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_DOUBLE_KEY = "{\"orig1\": \"old value\", \"orig2\": \"old value\", \"orig3\": \"value3\"}";

        Event event = EventBuilder.withBody(EVENT_DOUBLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String replacedKeys = new String(event.getBody());
        Assert.assertNotSame(EVENT_DOUBLE_KEY, replacedKeys);

        Assert.assertEquals("{\"orig1\":\"new value\",\"orig2\":\"new value\",\"orig3\":\"value3\"}", replacedKeys);
    }

    @Test
    public void interceptNotJsonKey() throws Exception {
        Context ctx = new Context();
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.REPLACEMENTS_CONF_NAME, "orig1#old value>new value;orig2#old value>new value");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.FIELD_DELIMITER_CONF_NAME, "#");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.VALUES_DELIMITER_CONF_NAME, ">");
        ctx.put(JsonFieldValueReplacerInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_NOT_JSON = "orig1";

        Event event = EventBuilder.withBody(EVENT_NOT_JSON, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String replacedKeys = new String(event.getBody());

        Assert.assertEquals(EVENT_NOT_JSON, replacedKeys);
    }

}