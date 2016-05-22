package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

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

    public KeyValueStorageMetricsService(StatsService statsService, String store, String job) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("job", job);

        this.metrics = new KeyValueStorageMetrics(statsService, attributes);
    }
}
