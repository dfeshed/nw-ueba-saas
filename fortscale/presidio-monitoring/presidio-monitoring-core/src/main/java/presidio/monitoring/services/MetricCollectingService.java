package presidio.monitoring.services;

import presidio.monitoring.records.Metric;

public interface MetricCollectingService {

    void addMetric(Metric metric);

}
