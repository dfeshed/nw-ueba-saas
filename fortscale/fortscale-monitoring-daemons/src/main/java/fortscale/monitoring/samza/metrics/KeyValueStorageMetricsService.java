package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class KeyValueStorageMetricsService {
    KeyValueStorageMetrics metrics;

    public KeyValueStorageMetrics getKeyValueStorageMetrics() {
        return metrics;
    }

    public KeyValueStorageMetricsService(StatsService statsService, String store, String jobName) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("task", jobName);
        this.metrics = new KeyValueStorageMetrics(statsService, attributes);
    }
}
