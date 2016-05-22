package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class KeyValueChangeLogTopicMetricsService {
    KeyValueChangeLogTopicMetrics metrics;

    public KeyValueChangeLogTopicMetrics getKeyValueChangeLogTopicMetrics() {
        return metrics;
    }

    public KeyValueChangeLogTopicMetricsService(StatsService statsService, String store,String job) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("job", job);
        attributes.addTag("store_job", String.format("%s__%s",store,job));

        this.metrics = new KeyValueChangeLogTopicMetrics(statsService, attributes);
    }
}
