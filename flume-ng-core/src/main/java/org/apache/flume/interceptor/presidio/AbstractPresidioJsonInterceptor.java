package org.apache.flume.interceptor.presidio;

import com.google.common.collect.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.flume.CommonStrings;
import org.apache.flume.Event;
import org.apache.flume.FlumePresidioExternalMonitoringService;
import org.apache.flume.FlumePresidioExternalMonitoringService.FlumeComponentType;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.conf.MonitorDetails;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.marker.MonitorUses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class adds support for 2 things:
 * 1) for running flume as a batch process (init, run, stop) and not as a stream process (which is the default behaviour). A Presidio sink/source must also be used when using a Presidio interceptor.
 * 2) for using a metric service (that needs an application name).
 */
public abstract class AbstractPresidioJsonInterceptor implements Interceptor, MonitorUses {

    private static final Logger logger = LoggerFactory
            .getLogger(AbstractPresidioJsonInterceptor.class);

    protected static final String EMPTY_STRING = "EMPTY_STRING";
    protected static final String NULL_STRING = "NULL";
    protected FlumePresidioExternalMonitoringService monitoringService;
    protected String applicationName;
    protected String interceptorName;

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
        monitoringService.reportTotalEventMetric(1);
        try {
            Event eventResult = doIntercept(event);
            if (eventResult == null) {
                //Do nothing, expected from the implementation to report why the events filtered.
            } else {
                monitoringService.reportSuccessEventMetric(1);
            }
            return eventResult;
        } catch (ConfigurationException e) {
            logger.error("Bad configuration in {}. Dropping event. Exception: ",
                    this.getClass().getName(), e);
            monitoringService.reportFailedEventMetric("FAILED_CONFIGURATION", 1);
            return null;
        } catch (Exception e) {
            logger.error("{} interception has failed. Dropping event. Exception: ",
                    this.getClass().getName(), e);
            monitoringService.reportFailedEventMetric("INTERCEPTION_FAILED", 1);
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

    public String getApplicationName() {
        return applicationName;
    }

    private boolean isGotControlDoneMessage(Event flumeEvent) {
        return BooleanUtils.toBoolean(flumeEvent.getHeaders().get(CommonStrings.IS_DONE));
    }


    @Override
    public void setMonitorDetails(MonitorDetails monitorDetails) {

        monitoringService = new FlumePresidioExternalMonitoringService(monitorDetails, FlumeComponentType.INTERCEPTOR, this.interceptorName);
    }
}
