package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

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

    public KeyValueStoreMetricsService(StatsService statsService,String job, String store) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("job", job);

        this.keyValueStoreMetrics = new KeyValueStoreMetrics(statsService, attributes);
    }
}
