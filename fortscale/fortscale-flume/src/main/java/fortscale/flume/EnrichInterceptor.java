package fortscale.flume;

import com.google.common.collect.ImmutableMap;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.LoggerFactory;
import org.apache.flume.interceptor.Interceptor;

import java.util.LinkedList;
import java.util.List;

/**
 * The class is an implementation of Flume API for custom interceptor
 * The class intercept each event and enrich it with key \ value pairs taken from Flume's configuration.
 *
 * For more information about Flume interceptors, please see the official documentation:
 * https://flume.apache.org/FlumeUserGuide.html#flume-interceptors
 */
public class EnrichInterceptor implements Interceptor {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EnrichInterceptor.class);

	/**
	 * Holds the enrichment data
	 */
	private ImmutableMap<String, String> enrichMap;

	public EnrichInterceptor(Context context) {
		enrichMap = context.getParameters();
	}

	@Override
	public void initialize() {	}

	/**
	 * Intercept single event
	 * @param event
	 * @return The enriched event
	 */
	@Override
	public Event intercept(Event event) {

		// Input check
		if (this.enrichMap == null || this.enrichMap.size() == 0) {
			logger.warn("Enriched data is not set. Events will not be enriched with extra data.");
			return event;
		}

		// Init handler
		//EnrichmentHandler handler = new EnrichmentHandler(event.getBody(), enrichMap);

		try {
			// Set the event body to contain the enrich data
			event.setBody(EnrichmentHandler.buildEventBody(event.getBody(), enrichMap));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return event;
	}

	/**
	 * Intercept list of events
	 * @param events
	 * @return List of enriched events
	 */
	@Override
	public List<Event> intercept(List<Event> events) {
		List<Event> out = new LinkedList<Event>();
		for (Event e : events) {
			out.add(intercept(e));
		}
		return out;
	}

	@Override
	public void close() {
        /* nothing to do, really */
	}

	/**
	 * Static class to implement Flume interceptor API
	 */
	public static class EnrichmentBuilder implements Interceptor.Builder {
		private Context ctx;

		@Override public Interceptor build() {
			return new EnrichInterceptor(ctx);
		}

		@Override public void configure(Context context) {
			this.ctx = context;
		}
	}
}
