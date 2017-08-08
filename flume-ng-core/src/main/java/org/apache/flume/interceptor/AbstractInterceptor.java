package org.apache.flume.interceptor;

import com.google.common.collect.Lists;
import org.apache.flume.Event;

import java.util.List;

public abstract class AbstractInterceptor implements Interceptor {

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
}
