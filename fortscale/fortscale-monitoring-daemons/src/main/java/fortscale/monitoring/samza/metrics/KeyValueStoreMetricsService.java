package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class KeyValueStoreMetricsService {
    KeyValueStoreMetrics keyValueStoreMetrics;

    public KeyValueStoreMetrics getKeyValueStoreMetrics() {
        return keyValueStoreMetrics;
    }

    public KeyValueStoreMetricsService(StatsService statsService,String jobName, String store) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("task", jobName);
        this.keyValueStoreMetrics = new KeyValueStoreMetrics(statsService, attributes);
    }
}
