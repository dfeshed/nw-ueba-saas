package fortscale.monitoring.external.stats.samza.collector.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * stats monitoring counters
 */
public class SamzaMetricCollectorMetricsService {
    SamzaMetricCollectorMetrics metrics;

    public SamzaMetricCollectorMetrics getMetrics() {
        return metrics;
    }

    public SamzaMetricCollectorMetricsService(StatsService statsService) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("process","SamzaMetricsCollector");
        this.metrics = new SamzaMetricCollectorMetrics(statsService, attributes);
    }


}
