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

/**
 * Created by tomerd on 9/10/2017.
 */
public class JsonArrayToElementInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;



    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_ARRAY_TO_SINGLE.toString());
    }



    @Test
    public void interceptToSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonArrayToElementInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, "array");
        ctx.put(JsonArrayToElementInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, "single");
        ctx.put(JsonArrayToElementInterceptor.Builder.INDEX, "0");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_ARRAY_KEY = "{\"array\":[\"elem1\",\"elem2\"]}";

        Event event = EventBuilder.withBody(EVENT_ARRAY_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());

        Assert.assertEquals("{\"array\":[\"elem1\",\"elem2\"],\"single\":\"elem1\"}", interceptValue);
    }

    @Test
    public void interceptToNoKeyIndexOutOfBound() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonArrayToElementInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, "array");
        ctx.put(JsonArrayToElementInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, "single");
        ctx.put(JsonArrayToElementInterceptor.Builder.INDEX, "2");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_ARRAY_KEY = "{\"array\":[\"elem1\",\"elem2\"]}";

        Event event = EventBuilder.withBody(EVENT_ARRAY_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());

        Assert.assertEquals("{\"array\":[\"elem1\",\"elem2\"]}", interceptValue);
    }

}