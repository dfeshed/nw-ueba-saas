package presidio.monitoring.sdk.api.services;


import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.Map;

public interface PresidioExternalMonitoringService {

    void reportCustomMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime);

    void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime);

    void manualExportMetrics();

}
