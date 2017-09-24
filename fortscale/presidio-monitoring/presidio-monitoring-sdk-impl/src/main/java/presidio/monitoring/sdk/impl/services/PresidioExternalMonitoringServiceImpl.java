package presidio.monitoring.sdk.impl.services;


import org.springframework.beans.factory.annotation.Autowired;
import presidio.monitoring.aspect.services.MetricCollectingServiceImpl;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.util.Set;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    @Autowired
    private MetricCollectingServiceImpl metricCollectingService;

    private final String FILTERED_EVENT_METRIC = "number.of.filtered.event";
    private final String NUMBER_OF_PROCESSED_EVENTS_METRIC = "number.of.processed.events";
    private final String TYPE_LONG = "long";

    public PresidioExternalMonitoringServiceImpl() {
    }

    @Override
    public void reportNumberOfFilteredEventMetric(long value, Set<String> tags) {
        metricCollectingService.addMetricWithTags(FILTERED_EVENT_METRIC, value, tags, TYPE_LONG);
    }

    @Override
    public void reportNumberOfProcessedEventsMetric(long value, Set<String> tags) {
        metricCollectingService.addMetricWithTags(NUMBER_OF_PROCESSED_EVENTS_METRIC, value, tags, TYPE_LONG);
    }

    @Override
    public void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType) {
        metricCollectingService.addMetricWithTags(metricName, value, tags, valueType);
    }
}
