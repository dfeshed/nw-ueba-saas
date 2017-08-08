package org.flume.interceptor.json;


import com.google.common.base.Charsets;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.InterceptorBuilderFactory;
import org.apache.flume.interceptor.InterceptorType;
import org.apache.flume.interceptor.TimestampInterceptor;
import org.junit.Assert;

/**
 * Created by tomerd on 8/7/2017.
 */
public class PresidioJsonRenamerInterceptorTest {
    @org.junit.Test
    public void intercept() throws Exception {
        InterceptorBuilderFactory factory = new InterceptorBuilderFactory();
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(
                InterceptorType..toString());
        Interceptor interceptor = builder.build();

        Event event = EventBuilder.withBody("test event", Charsets.UTF_8);
        Assert.assertNull(event.getHeaders().get(TimestampInterceptor.Constants.TIMESTAMP));

        Long now = System.currentTimeMillis();
        event = interceptor.intercept(event);
        String timestampStr = event.getHeaders().get(TimestampInterceptor.Constants.TIMESTAMP);
        Assert.assertNotNull(timestampStr);
        Assert.assertTrue(Long.parseLong(timestampStr) >= now);
    }

}