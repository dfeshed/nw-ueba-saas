package org.flume.interceptor.base;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;

public abstract class AbstractPresidioInterceptor implements Interceptor {

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
        for (Event event : events) {
            Event interceptedEvent = intercept(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void close() {

    }

    protected JsonObject getEventBodyAsJson(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        return new JsonParser().parse(eventBodyAsString).getAsJsonObject();
    }
}
