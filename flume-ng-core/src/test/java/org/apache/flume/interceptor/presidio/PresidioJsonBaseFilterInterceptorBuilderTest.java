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

public class PresidioJsonBaseFilterInterceptorBuilderTest {

    private Interceptor.Builder testSubjectBuilder;


    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        testSubjectBuilder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_BASE_FILTER.toString());
    }

    @Test
    public void doConfigureSingleField_EQUALS() throws Exception {
        Context ctx = new Context();
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS#value");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


        final String EVENT_SINGLE_KEY = "{\"orig1\":\"value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_NOT_EQUALS() throws Exception {
        Context ctx = new Context();
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "NOT_EQUALS#value");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


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
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS_IGNORE_CASE#value");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


        final String EVENT_SINGLE_KEY = "{\"orig1\":\"ValUe\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{}", interceptValue);
    }

    @Test
    public void doConfigureSingleField_EQUALS_CONTAINS() throws Exception {
        Context ctx = new Context();
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "CONTAINS#value");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


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
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "STARTS_WITH#value");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


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
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "ENDS_WITH#value");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


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
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS#|field|orig1");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


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
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "non-existing-field");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "EQUALS#|FIELD|another-non-existing-field");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


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
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.FIELDS_CONF_NAME, "orig1,orig1,orig2,orig3");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_CONF_NAME, "ENDS_WITH#value,EQUALS#Xvalue,CONTAINS#|field|orig3,STARTS_WITH#some-prefix");
        ctx.put(AbstractPresidioJsonFilterInterceptorBuilder.PREDICATES_PARAMS_DELIMITER_CONF_NAME, "#");

        testSubjectBuilder.configure(ctx);

        final Interceptor interceptor = testSubjectBuilder.build();


        final String EVENT_SINGLE_KEY = "{\"orig1\":\"Xvalue\",\"orig2\":\"XXsome-valueXX\",\"orig3\":\"some-value\"}";

        Event event = EventBuilder.withBody(EVENT_SINGLE_KEY, Charsets.UTF_8);

        event = interceptor.intercept(event);
        String interceptValue = new String(event.getBody());
        Assert.assertNotSame(EVENT_SINGLE_KEY, interceptValue);


        Assert.assertEquals("{\"orig3\":\"some-value\"}", interceptValue);
    }

}