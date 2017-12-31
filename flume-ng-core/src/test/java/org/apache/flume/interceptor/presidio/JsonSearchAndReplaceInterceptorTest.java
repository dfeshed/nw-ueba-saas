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

public class JsonSearchAndReplaceInterceptorTest {

    private Interceptor.Builder builder;
    private Interceptor interceptor;

    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_SEARCH_AND_REPLACE.toString());
    }


    @Test
    public void interceptMultipleKey() throws Exception {

        Context ctx = new Context();
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.FIELDS_CONF_NAME, "field1;field1;field2");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.SEARCH_PATTERNS_CONF_NAME, "\\\\s+;old;-");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.REPLACE_STRINGS_CONF_NAME, "_;new;_");
        ctx.put(JsonSearchAndReplaceInterceptor.Builder.DELIMITER_CONF_NAME, ";");

        builder.configure(ctx);

        interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);
        final String EVENT_MULTIPLE_KEY = "{\"field1\": \"old value\", \"field2\": \"some-value\"}";

        Event event = EventBuilder.withBody(EVENT_MULTIPLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_MULTIPLE_KEY, interceptValue);

        Assert.assertEquals("{\"field1\":\"new value\",\"field2\":\"some_value\"}", interceptValue);
    }

}