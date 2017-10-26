package presidio.monitoring.sdk.api.services;


import java.util.Date;
import java.util.Set;

public interface PresidioExternalMonitoringService {

    void reportCustomMetric(String metricName, long value, Set<String> tags, String valueType, Date logicTime);

    void reportCustomMetricReportOnce(String metricName, long value, Set<String> tags, String valueType, Date logicTime);
}
