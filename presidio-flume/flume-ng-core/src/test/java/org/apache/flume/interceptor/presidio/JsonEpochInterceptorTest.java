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


public class JsonEpochInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_EPOCH.toString());
    }

    @Test
    public void interceptSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonEpochInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, "origTime");
        ctx.put(JsonEpochInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, "destTime");
        ctx.put(JsonEpochInterceptor.Builder.ORIGIN_FORMAT_CONF_NAME,  JsonEpochInterceptor.DateFormats.MILLIS.name());


        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"origTime\":\"1526980932000\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);

        Assert.assertEquals("{\"origTime\":\"1526980932000\",\"destTime\":\"1526980932\"}", interceptValue);
    }

    @Test
    public void interceptSameKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonEpochInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, "origTime");
        ctx.put(JsonEpochInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, "origTime");
        ctx.put(JsonEpochInterceptor.Builder.ORIGIN_FORMAT_CONF_NAME,  JsonEpochInterceptor.DateFormats.MILLIS.name());


        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"origTime\":\"1526980932000\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);

        Assert.assertEquals("{\"origTime\":\"1526980932\"}", interceptValue);
    }

    @Test
    public void interceptFieldNotExists() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonEpochInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, "origTime");
        ctx.put(JsonEpochInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, "origTime");
        ctx.put(JsonEpochInterceptor.Builder.ORIGIN_FORMAT_CONF_NAME, JsonEpochInterceptor.DateFormats.MILLIS.name());


        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"time\":\"1526980932000\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);
        Assert.assertEquals(EVENT_SIGNLE_KEY, interceptValue);
    }

}