package org.apache.flume.interceptor;

import com.google.common.collect.Lists;
import org.apache.flume.Event;
import org.apache.flume.conf.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(AbstractInterceptor.class);

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
    public Event intercept(Event event) {
        try {
            return doIntercept(event);
        }
        catch (ConfigurationException e){
            logger.error("Bad configuration in {}. Dropping event. Exception: ",
                    this.getClass().getName(), e);
            return null;
        }
        catch (Exception e) {
            logger.error("{} interception had failed. Dropping event. Exception: ",
                    this.getClass().getName(), e);
            return null;
        }
    }

    public abstract Event doIntercept(Event event);

    @Override
    public void initialize() {

    }

    @Override
    public void close() {

    }
}
