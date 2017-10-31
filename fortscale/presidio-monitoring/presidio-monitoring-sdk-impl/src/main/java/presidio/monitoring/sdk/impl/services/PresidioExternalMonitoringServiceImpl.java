package presidio.monitoring.sdk.impl.services;


import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.services.MetricCollectingService;

import java.time.Instant;
import java.util.Set;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    private MetricCollectingService metricCollectingService;
    private PresidioMetricFactory presidioMetricFactory;


    public PresidioExternalMonitoringServiceImpl() {
    }

    public PresidioExternalMonitoringServiceImpl(MetricCollectingService metricCollectingService, PresidioMetricFactory presidioMetricFactory) {
        this.presidioMetricFactory = presidioMetricFactory;
        this.metricCollectingService = metricCollectingService;
    }

    @Override
    public void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType, Instant logicTime) {
        metricCollectingService.addMetric(presidioMetricFactory.creatingPresidioMetric(metricName, value, tags, valueType, logicTime));
    }

    @Override
    public void reportCustomMetricReportOnce(String metricName, long value, Set<String> tags, String valueType, Instant logicTime) {
        metricCollectingService.addMetric(presidioMetricFactory.creatingPresidioMetric(metricName, value, tags, valueType, logicTime, true));
    }
}
