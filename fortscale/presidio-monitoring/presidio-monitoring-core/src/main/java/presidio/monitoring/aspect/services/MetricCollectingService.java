package presidio.monitoring.aspect.services;

import java.util.Set;

public interface MetricCollectingService {

    void addMetricWithTags(String metricName, long metricValue, Set tags, String unit);

    void addMetricWithOneTag(String metricName, long metricValue, String tags, String unit);
}
