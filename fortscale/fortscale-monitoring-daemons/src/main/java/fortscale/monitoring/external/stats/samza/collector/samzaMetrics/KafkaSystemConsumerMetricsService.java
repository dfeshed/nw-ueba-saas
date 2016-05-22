package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

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

    public KafkaSystemConsumerMetricsService(StatsService statsService,String job, String topic) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("topic", topic);
        attributes.addTag("job",job);

        this.kafkaSystemConsumerMetrics = new KafkaSystemConsumerMetrics(statsService, attributes);
    }
}
