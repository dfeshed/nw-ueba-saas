package presidio.monitoring.aspect.services;

import presidio.monitoring.elastic.records.PresidioMetric;

public interface MetricCollectingService {

    void addMetric(PresidioMetric presidioMetric);

    void addMetricReportOnce(PresidioMetric presidioMetric);
}
