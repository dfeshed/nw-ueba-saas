package org.apache.flume.interceptor.presidio;

import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.InterceptorBuilderFactory;
import org.apache.flume.interceptor.InterceptorType;
import org.junit.Assert;
import org.junit.Before;

/**
 * Created by tomerd on 8/8/2017.
 */
public class JsonFieldValueReplacerInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_FIELD_VALUE_REPLACER.toString());
    }

    //@Test
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
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"new value\"}", interceptValue);
    }

    //@Test
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
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_DOUBLE_KEY, interceptValue);

        Assert.assertEquals("{\"orig1\":\"new value\",\"orig2\":\"new value\",\"orig3\":\"value3\"}", interceptValue);
    }

    //@Test
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

        Assert.assertNull(event);
    }

}