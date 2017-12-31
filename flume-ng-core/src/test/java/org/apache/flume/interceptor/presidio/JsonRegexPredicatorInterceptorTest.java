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
public class JsonRegexPredicatorInterceptorTest {
    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_REGEX_PREDICATOR.toString());
    }

    @Test
    public void interceptRegex() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonRegexPredicatorInterceptor.Builder.VALUE_FIELDS_CONF_NAME, "path;ip");
        ctx.put(JsonRegexPredicatorInterceptor.Builder.PREDICATOR_FIELDS_CONF_NAME, "isSharedFolder;isIp");
        ctx.put(JsonRegexPredicatorInterceptor.Builder.REGEX_CONF_NAME, "^\\\\.*;\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
        ctx.put(JsonRegexPredicatorInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_SINGLE_KEY = "{\"path\": \"\\\\ISILON8\\ifs\\3.68.11.1.txt\", \"ip\": \"127.0.0.1\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"path\":\"\\\\ISILON8ifs3.68.11.1.txt\",\"ip\":\"127.0.0.1\",\"isSharedFolder\":true,\"isIp\":true}", interceptValue);
    }

}