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
public class JsonFieldRenamerInterceptorTest {
    Interceptor.Builder builder;
    Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_FILTER.toString());
    }


    @Test
    public void interceptSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonFieldRenamerInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "orig1;orig2");
        ctx.put(JsonFieldRenamerInterceptor.Builder.DESTINATION_FIELDS_CONF_NAME, "dest1;dest2");
        ctx.put(JsonFieldRenamerInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"orig1\":\"value\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String replacedKeys = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, replacedKeys);


        Assert.assertEquals("{\"dest1\":\"value\"}", replacedKeys);
    }

    @Test
    public void interceptDoubleKey() throws Exception {
        Context ctx = new Context();
        ctx.put(JsonFieldRenamerInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "orig1;orig2");
        ctx.put(JsonFieldRenamerInterceptor.Builder.DESTINATION_FIELDS_CONF_NAME, "dest1;dest2");
        ctx.put(JsonFieldRenamerInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_DOUBLE_KEY = "{\"orig1\": \"value1\", \"orig2\": \"value2\", \"orig3\": \"value3\"}";

        Event event = EventBuilder.withBody(EVENT_DOUBLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String replacedKeys = new String(event.getBody());
        Assert.assertNotSame(EVENT_DOUBLE_KEY, replacedKeys);


        Assert.assertEquals("{\"orig3\":\"value3\",\"dest1\":\"value1\",\"dest2\":\"value2\"}", replacedKeys);
    }

    @Test
    public void interceptNotJsonKey() throws Exception {
        Context ctx = new Context();
        ctx.put(JsonFieldRenamerInterceptor.Builder.ORIGIN_FIELDS_CONF_NAME, "orig1;orig2");
        ctx.put(JsonFieldRenamerInterceptor.Builder.DESTINATION_FIELDS_CONF_NAME, "dest1;dest2");
        ctx.put(JsonFieldRenamerInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_NOT_JSON = "orig1";

        Event event = EventBuilder.withBody(EVENT_NOT_JSON, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String replacedKeys = new String(event.getBody());

        Assert.assertEquals(EVENT_NOT_JSON, replacedKeys);
    }

}