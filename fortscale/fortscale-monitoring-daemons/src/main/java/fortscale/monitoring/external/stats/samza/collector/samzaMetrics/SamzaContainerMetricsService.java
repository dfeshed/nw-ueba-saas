package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class SamzaContainerMetricsService {
    SamzaContainerMetrics metrics;

    public SamzaContainerMetrics getSamzaContainerMetrics() {
        return metrics;
    }

    public SamzaContainerMetricsService(StatsService statsService, String job) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("job", job);
        this.metrics = new SamzaContainerMetrics(statsService, attributes);
    }
}
