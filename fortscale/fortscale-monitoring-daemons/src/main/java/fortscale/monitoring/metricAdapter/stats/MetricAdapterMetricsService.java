package fortscale.monitoring.metricAdapter.stats;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * metric adapter stats monitoring counters
 */
public class MetricAdapterMetricsService {
    MetricAdapterMetrics metrics;

    public MetricAdapterMetrics getMetrics() {
        return metrics;
    }

    public MetricAdapterMetricsService(StatsService statsService) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("process","metricAdapter");
        this.metrics = new MetricAdapterMetrics(statsService, attributes);
    }


}
