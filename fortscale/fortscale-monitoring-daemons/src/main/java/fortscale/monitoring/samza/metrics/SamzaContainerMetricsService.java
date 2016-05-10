package fortscale.monitoring.samza.metrics;

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

    public SamzaContainerMetricsService(StatsService statsService, String task) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("task", task);
        this.metrics = new SamzaContainerMetrics(statsService, attributes);
    }
}
