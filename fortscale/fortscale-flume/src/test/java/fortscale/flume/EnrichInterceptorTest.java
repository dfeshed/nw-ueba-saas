package fortscale.flume;

import junit.framework.TestCase;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 24/06/2015.
 */
public class EnrichInterceptorTest extends TestCase {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EnrichInterceptorTest.class);

	private Event createEvent(String message) {
		//Map<String, String> headers = new HashMap<String, String>();
		//headers.put("Key", "Value");
		//return EventBuilder.withBody(message.getBytes(), headers);
		return EventBuilder.withBody(message.getBytes());
	}

	private EnrichInterceptor createInterceptor() {
		Context context = new Context();
		context.put("timezone", "UTC");
		context.put("Data source", "America");

		EnrichInterceptor.EnrichmentBuilder builder = new EnrichInterceptor.EnrichmentBuilder();
		builder.configure(context);
		EnrichInterceptor interceptor = (EnrichInterceptor) builder.build();
		interceptor.initialize();
		return interceptor;
	}

	public void testIntercept() throws Exception {

	}

	@Test public void testSingleInterception() {
		Event event = createEvent("hello");
		String originalMessage = new String(event.getBody());

		EnrichInterceptor interceptor = createInterceptor();

		interceptor.intercept(event);

		String enrichedMessage = new String(event.getBody());

		logger.info("original message is: " + originalMessage);
		logger.info("enriched message is: " + enrichedMessage);
		Assert.assertNotEquals(originalMessage, enrichedMessage);
	}

	@Test public void testListInterception() {
		Event e1 = createEvent("hello1");
		Event e2 = createEvent("hello2");
		List<Event> eventList = new LinkedList<Event>();
		eventList.add(e1);
		eventList.add(e2);
		int size = eventList.size();

		EnrichInterceptor interceptor = createInterceptor();

		List<Event> interceptedList = interceptor.intercept(eventList);

		Assert.assertEquals(size, interceptedList.size());
	}
}