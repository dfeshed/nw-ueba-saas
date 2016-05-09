package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class KafkaSystemConsumerMetricsService {
    KafkaSystemConsumerMetrics kafkaSystemConsumerMetrics;

    public KafkaSystemConsumerMetrics getKafkaSystemConsumerMetrics() {
        return kafkaSystemConsumerMetrics;
    }

    public KafkaSystemConsumerMetricsService(StatsService statsService, String topic) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("topic", topic);
        this.kafkaSystemConsumerMetrics = new KafkaSystemConsumerMetrics(statsService, attributes);
    }
}
