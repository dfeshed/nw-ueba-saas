package org.apache.flume.interceptor.presidio;

import com.google.common.collect.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Event;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;

import java.util.List;

public abstract class AbstractInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(AbstractInterceptor.class);
    public static final String ADAPTER = "adapter";

    private static PresidioExternalMonitoringService presidioExternalMonitoringService;
    private PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory;

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
        if (isGotControlDoneMessage(event)) {
            return event;
        }
        try {
            return doIntercept(event);
        } catch (ConfigurationException e) {
            logger.error("Bad configuration in {}. Dropping event. Exception: ",
                    this.getClass().getName(), e);
            return null;
        } catch (Exception e) {
            logger.error("{} interception has failed. Dropping event. Exception: ",
                    this.getClass().getName(), e);
            return null;
        }
    }

    public abstract Event doIntercept(Event event);

    @Override
    public void initialize() {
        try {
            presidioExternalMonitoringService = presidioExternalMonitoringServiceFactory.
                    createPresidioExternalMonitoringService(ADAPTER);
        } catch (Exception e) {
            final String errorMessage = "Failed to start " + this.getClass().getSimpleName();
            logger.error(errorMessage, e);
        }
    }

    @Override
    public void close() {

    }


    private boolean isGotControlDoneMessage(Event flumeEvent) {
        return BooleanUtils.toBoolean(flumeEvent.getHeaders().get(CommonStrings.IS_DONE));
    }
}
