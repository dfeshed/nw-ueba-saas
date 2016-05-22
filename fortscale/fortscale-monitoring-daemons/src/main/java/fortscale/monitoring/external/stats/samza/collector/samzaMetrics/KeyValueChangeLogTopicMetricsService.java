package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;


public class KeyValueChangeLogTopicMetricsService {
    KeyValueChangeLogTopicMetrics metrics;

    public KeyValueChangeLogTopicMetrics getKeyValueChangeLogTopicMetrics() {
        return metrics;
    }

    public KeyValueChangeLogTopicMetricsService(StatsService statsService, String store,String job) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("store", store);
        attributes.addTag("job", job);

        this.metrics = new KeyValueChangeLogTopicMetrics(statsService, attributes);
    }
}
