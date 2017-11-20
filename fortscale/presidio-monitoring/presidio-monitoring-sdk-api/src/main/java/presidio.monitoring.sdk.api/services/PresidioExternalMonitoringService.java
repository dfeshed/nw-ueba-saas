package presidio.monitoring.sdk.api.services;


import presidio.monitoring.enums.MetricEnums;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public interface PresidioExternalMonitoringService {

    void reportCustomMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String valueType, Instant logicTime);

    void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String valueType, Instant logicTime);

}
