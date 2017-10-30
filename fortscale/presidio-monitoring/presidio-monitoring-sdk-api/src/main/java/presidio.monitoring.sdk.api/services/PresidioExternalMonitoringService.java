package presidio.monitoring.sdk.api.services;


import presidio.monitoring.records.Metric;

public interface PresidioExternalMonitoringService {

    void reportCustomMetric(Metric metric);
}
