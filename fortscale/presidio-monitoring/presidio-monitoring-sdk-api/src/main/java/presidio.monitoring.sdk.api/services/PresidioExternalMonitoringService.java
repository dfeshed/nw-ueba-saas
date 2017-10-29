package presidio.monitoring.sdk.api.services;


import presidio.monitoring.elastic.records.PresidioMetric;

public interface PresidioExternalMonitoringService {

    void reportCustomMetric(PresidioMetric presidioMetric);

    void reportCustomMetricReportOnce(PresidioMetric presidioMetric);
}
