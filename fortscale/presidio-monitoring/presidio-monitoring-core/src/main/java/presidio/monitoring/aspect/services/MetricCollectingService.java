package presidio.monitoring.aspect.services;

import java.util.Set;

public interface MetricCollectingService {

    void addMetric(String metricName, long metricValue, Set tags, String unit);
}
