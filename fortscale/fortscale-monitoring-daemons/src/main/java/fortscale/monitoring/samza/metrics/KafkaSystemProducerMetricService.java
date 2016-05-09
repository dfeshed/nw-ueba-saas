package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by cloudera on 5/8/16.
 */
public class KafkaSystemProducerMetricService {
    KafkaSystemProducerMetric kafkaSystemProducerMetric;

    public KafkaSystemProducerMetric getKafkaSystemProducerMetric() {
        return kafkaSystemProducerMetric;
    }

    public KafkaSystemProducerMetricService(StatsService statsService, String topic) {
        StatsMetricsGroupAttributes attributes = new StatsMetricsGroupAttributes();
        attributes.addTag("topic", topic);
        this.kafkaSystemProducerMetric = new KafkaSystemProducerMetric(statsService, attributes);
    }
}
