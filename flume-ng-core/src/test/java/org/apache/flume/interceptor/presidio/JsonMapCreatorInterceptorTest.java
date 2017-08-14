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
 * Created by tomerd on 8/14/2017.
 */
public class JsonMapCreatorInterceptorTest {
    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_MAP_CREATOR.toString());
    }

    @Test
    public void interceptSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonMapCreatorInterceptor.Builder.FIELDS_TO_JOIN_CONF_NAME, "name,ip");
        ctx.put(JsonMapCreatorInterceptor.Builder.MAP_KEY_NAME_CONF_NAME, "additionalInfo");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SIGNLE_KEY = "{\"name\": \"user\", \"ip\": \"127.0.0.1\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"additionalInfo\":{\"name\":\"user\",\"ip\":\"127.0.0.1\"}}", interceptValue);
    }

}