package presidio.monitoring.services;

import presidio.monitoring.sdk.api.services.model.Metric;

public interface MetricCollectingService {

    void addMetric(Metric metric);

}
