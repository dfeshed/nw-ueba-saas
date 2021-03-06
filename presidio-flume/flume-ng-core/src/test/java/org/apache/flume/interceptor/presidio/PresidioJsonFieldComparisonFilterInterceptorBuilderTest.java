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

public class PresidioJsonFieldComparisonFilterInterceptorBuilderTest {

    private Interceptor.Builder testSubjectBuilder;


    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        testSubjectBuilder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_FIELD_COMPARISON_FILTER.toString());
    }

    @Test
    public void doConfigureSingleField_EQUALS() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1,orig2");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS#orig2,EQUALS#orig3");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"ValUe\",\"orig2\":\"Value\",\"orig3\":\"Value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"ValUe\",\"orig3\":\"Value\"}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_NOT_EQUALS() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "NOT_EQUALS#value");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"not-value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_EQUALS_IGNORE_CASE() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS_IGNORE_CASE#orig2");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"ValUe\",\"orig2\":\"Value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig2\":\"Value\"}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_EQUALS_CONTAINS() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "CONTAINS#value");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"XvalueX\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_STARTS_WITH() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "STARTS_WITH#value");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"valueX\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_ENDS_WITH() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "ENDS_WITH#value");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"Xvalue\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_EQUALS_WITH_FIELD() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS#orig1");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleFieldEmptyField() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "non-existing-field");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS#another-non-existing-field");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"Xvalue\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig1\":\"Xvalue\"}", interceptValue);
    }

    @Test
    public void doConfigureMultipleFields() throws Exception {
        Context ctx = new Context();
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1,orig3,orig2,orig4");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "NOT_EQUALS#orig2,CONTAINS#orig2,STARTS_WITH#orig4,ENDS_WITH#orig4");
        ctx.put(PresidioJsonFieldComparisonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        final String EVENT_SINGLE_KEY = "{\"orig1\":\"Xvalue\",\"orig2\":\"XXsome-valueXX\",\"orig3\":\"some-value\",\"orig4\":\"XXsome-value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig3\":\"some-value\"}", interceptValue);
    }

}