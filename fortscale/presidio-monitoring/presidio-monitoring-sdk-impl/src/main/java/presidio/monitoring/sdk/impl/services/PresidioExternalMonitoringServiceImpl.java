package presidio.monitoring.sdk.impl.services;


import presidio.monitoring.enums.MetricEnums;
import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.services.MetricCollectingService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    private MetricCollectingService metricCollectingService;
    private PresidioMetricFactory presidioMetricFactory;


    public PresidioExternalMonitoringServiceImpl(MetricCollectingService metricCollectingService, PresidioMetricFactory presidioMetricFactory) {
        this.presidioMetricFactory = presidioMetricFactory;
        this.metricCollectingService = metricCollectingService;
    }

    @Override
    public void reportCustomMetric(String metricName, Number value, Set<String> tags, String valueType, Instant logicTime) {
        Map<MetricEnums.MetricValues, Number> valuesMap = new HashMap<>();
        valuesMap.put(MetricEnums.MetricValues.SUM, value);
        metricCollectingService.addMetric(presidioMetricFactory.creatingPresidioMetric(metricName, valuesMap, tags, valueType, logicTime));
    }

    @Override
    public void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Set<String> tags, String valueType, Instant logicTime) {
        metricCollectingService.addMetric(presidioMetricFactory.creatingPresidioMetric(metricName, value, tags, valueType, logicTime));

    }
}
