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
import org.junit.Test;

/**
 * Created by tomerd on 9/10/2017.
 */
public class JsonCaseInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_CASE.toString());
    }

    @Test
    public void interceptToUpperKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonCaseInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "orig1");
        ctx.put(JsonCaseInterceptor.Builder.OPERATIONS_CONF_NAME, "TO_UPPERCASE");
        ctx.put(JsonCaseInterceptor.Builder.DELIMITER_CONF_NAME, ",");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"orig1\":\"value\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"VALUE\"}", interceptValue);
    }

    @Test
    public void interceptToLowerKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonCaseInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "orig1");
        ctx.put(JsonCaseInterceptor.Builder.OPERATIONS_CONF_NAME, "TO_LOWERCASE");
        ctx.put(JsonCaseInterceptor.Builder.DELIMITER_CONF_NAME, ",");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"orig1\":\"VALUE\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"value\"}", interceptValue);
    }


    @Test
    public void interceptDoubleKeys() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonCaseInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "orig1,orig2");
        ctx.put(JsonCaseInterceptor.Builder.OPERATIONS_CONF_NAME, "TO_LOWERCASE,TO_LOWERCASE");
        ctx.put(JsonCaseInterceptor.Builder.DELIMITER_CONF_NAME, ",");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"orig1\":\"VALUE1\",\"orig2\":\"VALUE2\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"value1\",\"orig2\":\"value2\"}", interceptValue);
    }

    @Test
    public void nonExistsField() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonCaseInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "nonexistsfield");
        ctx.put(JsonCaseInterceptor.Builder.OPERATIONS_CONF_NAME, "TO_LOWERCASE");
        ctx.put(JsonCaseInterceptor.Builder.DELIMITER_CONF_NAME, ",");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"orig1\":\"VALUE\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"VALUE\"}", interceptValue);
    }
}