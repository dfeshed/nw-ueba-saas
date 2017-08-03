package presidio.monitoring.aspect.services;

import java.util.Set;

public interface MetricCollectingService {

    void addMtric(String metricName, double metricValue, Set tags, String unit);
}
