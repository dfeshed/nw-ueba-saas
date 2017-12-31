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
        ctx.put(JsonMapCreatorInterceptor.Builder.FIELDS_TO_PUT_CONF_NAME, "name,ip");
        ctx.put(JsonMapCreatorInterceptor.Builder.MAP_KEY_NAME_CONF_NAME, "additionalInfo");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"name\": \"user\", \"ip\": \"127.0.0.1\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"additionalInfo\":{\"name\":\"user\",\"ip\":\"127.0.0.1\"}}", interceptValue);
    }

    @Test
    public void interceptNoFieldButDefaultValueExists() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonMapCreatorInterceptor.Builder.FIELDS_TO_PUT_CONF_NAME, "isUserAdmin,name,ip");
        ctx.put(JsonMapCreatorInterceptor.Builder.MAP_KEY_NAME_CONF_NAME, "additionalInfo");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");
        ctx.put(JsonMapCreatorInterceptor.Builder.DEFAULT_VALUES_DELIMITER_CONF_NAME, ">");
        ctx.put(JsonMapCreatorInterceptor.Builder.DEFAULT_VALUES_CONF_NAME, "isUserAdmin>false");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"name\": \"user\", \"ip\": \"127.0.0.1\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"additionalInfo\":{\"isUserAdmin\":\"false\",\"name\":\"user\",\"ip\":\"127.0.0.1\"}}", interceptValue);
    }

    @Test
    public void interceptWithFieldButDefaultValueExists() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonMapCreatorInterceptor.Builder.FIELDS_TO_PUT_CONF_NAME, "isUserAdmin,name,ip");
        ctx.put(JsonMapCreatorInterceptor.Builder.MAP_KEY_NAME_CONF_NAME, "additionalInfo");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");
        ctx.put(JsonMapCreatorInterceptor.Builder.DEFAULT_VALUES_DELIMITER_CONF_NAME, ">");
        ctx.put(JsonMapCreatorInterceptor.Builder.DEFAULT_VALUES_CONF_NAME, "isUserAdmin>false");


        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"name\": \"user\", \"ip\": \"127.0.0.1\",\"isUserAdmin\":\"true\"}";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"additionalInfo\":{\"isUserAdmin\":\"true\",\"name\":\"user\",\"ip\":\"127.0.0.1\"}}", interceptValue);
    }

    @Test
    public void interceptAndAddToExistingMap() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonMapCreatorInterceptor.Builder.FIELDS_TO_PUT_CONF_NAME, "isUserAdmin,name,ip");
        ctx.put(JsonMapCreatorInterceptor.Builder.MAP_KEY_NAME_CONF_NAME, "additionalInfo");
        ctx.put(JsonMapCreatorInterceptor.Builder.DELETE_FIELDS_CONF_NAME, "true");
        ctx.put(JsonMapCreatorInterceptor.Builder.OVERRIDE_EXISTING_MAP_NAME, "false");
        ctx.put(JsonMapCreatorInterceptor.Builder.DEFAULT_VALUES_DELIMITER_CONF_NAME, ">");
        ctx.put(JsonMapCreatorInterceptor.Builder.DEFAULT_VALUES_CONF_NAME, "isUserAdmin>false");


        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SIGNLE_KEY = "{\"name\": \"user\", \"ip\": \"127.0.0.1\",\"isUserAdmin\":\"true\", \"additionalInfo\" : { \"operationType\" : \"USER_AUTHENTICATED_THROUGH_KERBEROS\" } }";

        Event event = EventBuilder.withBody(EVENT_SIGNLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SIGNLE_KEY, interceptValue);


        Assert.assertEquals("{\"additionalInfo\":{\"operationType\":\"USER_AUTHENTICATED_THROUGH_KERBEROS\",\"isUserAdmin\":\"true\",\"name\":\"user\",\"ip\":\"127.0.0.1\"}}", interceptValue);
    }
}