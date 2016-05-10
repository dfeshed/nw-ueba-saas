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

    public KeyValueChangeLogTopicMetricsService(StatsService statsService, String store,String jobName) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("jobName", jobName);
        this.metrics = new KeyValueChangeLogTopicMetrics(statsService, attributes);
    }
}
