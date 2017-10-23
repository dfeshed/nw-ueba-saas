package presidio.monitoring.sdk.impl.services;


import org.springframework.beans.factory.annotation.Autowired;
import presidio.monitoring.aspect.services.MetricCollectingService;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.util.Date;
import java.util.Set;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    @Autowired
    private MetricCollectingService metricCollectingService;

    private final String FILTERED_EVENT_METRIC = "number.of.filtered.event";
    private final String NUMBER_OF_PROCESSED_EVENTS_METRIC = "number.of.processed.events";
    private final String TYPE_LONG = "long";

    public PresidioExternalMonitoringServiceImpl() {
    }

    @Override
    public void reportNumberOfFilteredEventMetric(long value, Set<String> tags, Date logicTime) {
        metricCollectingService.addMetric(FILTERED_EVENT_METRIC, value, tags, TYPE_LONG, logicTime);
    }

    @Override
    public void reportNumberOfProcessedEventsMetric(long value, Set<String> tags, Date logicTime) {
        metricCollectingService.addMetric(NUMBER_OF_PROCESSED_EVENTS_METRIC, value, tags, TYPE_LONG, logicTime);
    }

    @Override
    public void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType, Date logicTime) {
        metricCollectingService.addMetric(metricName, value, tags, valueType, logicTime);
    }
}
