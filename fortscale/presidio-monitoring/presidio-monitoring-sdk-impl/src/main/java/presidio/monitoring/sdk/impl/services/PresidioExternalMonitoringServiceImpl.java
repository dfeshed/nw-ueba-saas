package presidio.monitoring.sdk.impl.services;


import presidio.monitoring.aspect.services.MetricCollectingService;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.util.Date;
import java.util.Set;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    private MetricCollectingService metricCollectingService;

    private final String FILTERED_EVENT_METRIC = "number.of.filtered.event";
    private final String NUMBER_OF_PROCESSED_EVENTS_METRIC = "number.of.processed.events";
    private final String TYPE_LONG = "long";


    public PresidioExternalMonitoringServiceImpl() {
    }

    public PresidioExternalMonitoringServiceImpl(MetricCollectingService metricCollectingService) {
        this.metricCollectingService = metricCollectingService;
    }

    @Override
    public void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType, Date logicTime) {
        metricCollectingService.addMetric(metricName, value, tags, valueType, logicTime);
    }

    @Override
    public void reportCustomMetricReportOnce(String metricName, long value, Set<String> tags, String valueType, Date logicTime) {
        metricCollectingService.addMetricReportOnce(metricName, value, tags, valueType, logicTime);
    }


}
