package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class KafkaSystemProducerMetricsService {
    KafkaSystemProducerMetrics kafkaSystemProducerMetrics;

    public KafkaSystemProducerMetrics getKafkaSystemProducerMetrics() {
        return kafkaSystemProducerMetrics;
    }

    public KafkaSystemProducerMetricsService(StatsService statsService,String jobName) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("task", jobName);
        this.kafkaSystemProducerMetrics = new KafkaSystemProducerMetrics(statsService, attributes);
    }
}
