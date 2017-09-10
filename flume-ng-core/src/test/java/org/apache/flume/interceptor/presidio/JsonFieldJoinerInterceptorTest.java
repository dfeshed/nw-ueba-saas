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
 * Created by tomerd on 8/9/2017.
 */
public class JsonFieldJoinerInterceptorTest {
    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_FIELD_JOINER.toString());
    }

    @Test
    public void interceptSingleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonFieldJoinerInterceptor.Builder.BASE_FIELD_CONF_NAME, "folderName");
        ctx.put(JsonFieldJoinerInterceptor.Builder.TO_APPEND_FIELD_CONF_NAME, "fileName");
        ctx.put(JsonFieldJoinerInterceptor.Builder.TARGET_FIELD_CONF_NAME, "fullPath");
        ctx.put(JsonFieldJoinerInterceptor.Builder.FILTER_ON_MISSING_BASE_FIELD_CONF_NAME, "true");
        ctx.put(JsonFieldJoinerInterceptor.Builder.FILTER_ON_MISSING_TO_APPEND_FIELD_CONF_NAME, "true");
        ctx.put(JsonFieldJoinerInterceptor.Builder.REMOVE_BASE_FIELD_CONF_NAME, "true");
        ctx.put(JsonFieldJoinerInterceptor.Builder.REMOVE_TO_APPEND_CONF_NAME, "true");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_SINGLE_KEY = "{\"folderName\":\"folder\",\"fileName\":\"file.txt\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"fullPath\":\"folderfile.txt\"}", interceptValue);
    }

    @Test
    public void interceptDoubleKey() throws Exception {
        Context ctx = new Context();
        ctx.put(JsonFieldJoinerInterceptor.Builder.BASE_FIELD_CONF_NAME, "folderName");
        ctx.put(JsonFieldJoinerInterceptor.Builder.TO_APPEND_FIELD_CONF_NAME, "fileName");
        ctx.put(JsonFieldJoinerInterceptor.Builder.TARGET_FIELD_CONF_NAME, "fullPath");
        ctx.put(JsonFieldJoinerInterceptor.Builder.REMOVE_BASE_FIELD_CONF_NAME, "true");
        ctx.put(JsonFieldJoinerInterceptor.Builder.REMOVE_TO_APPEND_CONF_NAME, "true");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_DOUBLE_KEY = "{\"folderName\":\"folder\",\"fileName\":\"file.txt\",\"fullPath\":\"file.txt\"}";

        Event event = EventBuilder.withBody(EVENT_DOUBLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_DOUBLE_KEY, interceptValue);

        Assert.assertEquals("{\"fullPath\":\"folderfile.txt\"}", interceptValue);
    }

    @Test
    public void interceptNotJsonKey() throws Exception {
        Context ctx = new Context();
        ctx.put(JsonFieldJoinerInterceptor.Builder.BASE_FIELD_CONF_NAME, "folderName");
        ctx.put(JsonFieldJoinerInterceptor.Builder.TO_APPEND_FIELD_CONF_NAME, "fileName");
        ctx.put(JsonFieldJoinerInterceptor.Builder.TARGET_FIELD_CONF_NAME, "fullPath");
        ctx.put(JsonFieldJoinerInterceptor.Builder.REMOVE_BASE_FIELD_CONF_NAME, "true");
        ctx.put(JsonFieldJoinerInterceptor.Builder.REMOVE_TO_APPEND_CONF_NAME, "true");

        builder.configure(ctx);

        interceptor = builder.build();

        final String EVENT_NOT_JSON = "orig1";

        Event event = EventBuilder.withBody(EVENT_NOT_JSON, Charsets.UTF_8);

        event = interceptor.intercept(event);

        Assert.assertNull(event);
    }

}