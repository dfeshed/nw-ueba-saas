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


public class JsonTimestampWithOffsetFormatterInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_TIMESTAMP_WITH_OFFSET.toString());
    }

    @Test
    public void interceptSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonTimestampWithOffsetFormatterInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, "origTime");
        ctx.put(JsonTimestampWithOffsetFormatterInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, "destTime");
        ctx.put(JsonTimestampWithOffsetFormatterInterceptor.Builder.TIMEZONE_OFFSET_FIELD_CONF_NAME, "offset");
        ctx.put(JsonTimestampWithOffsetFormatterInterceptor.Builder.ORIGIN_FORMAT_CONF_NAME, "M/dd/yyyy h:mm:ss a");
        ctx.put(JsonTimestampWithOffsetFormatterInterceptor.Builder.DESTINATION_FORMAT_CONF_NAME, "yyyy-MM-dd'T'hh:mm:ss");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"origTime\": \"7/25/2017 5:34:35 PM\", \"offset\": \"1\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"destTime\":\"2017-07-25T05:34:35\"}", interceptValue);
    }
}