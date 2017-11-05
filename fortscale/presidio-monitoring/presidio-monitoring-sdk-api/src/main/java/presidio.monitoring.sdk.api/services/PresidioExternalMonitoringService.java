package presidio.monitoring.sdk.api.services;


import java.time.Instant;
import java.util.Set;

public interface PresidioExternalMonitoringService {

    void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType, Instant logicTime);

    void reportCustomMetricReportOnce(String metricName, long value, Set<String> tags, String valueType, Instant logicTime);
}
