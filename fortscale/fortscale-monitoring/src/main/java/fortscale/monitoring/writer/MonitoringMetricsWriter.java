package fortscale.monitoring.writer;

import fortscale.utils.kafka.KafkaEventsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gils
 * 20/03/2016
 */
public class MonitoringMetricsWriter {
    private static Logger logger = LoggerFactory.getLogger(MonitoringMetricsWriter.class);

    private static final String METRICS_TOPIC_NAME = "metrics";

    public void writeMetric(String metricValue) {
        KafkaEventsWriter kafkaEventsWriter = new KafkaEventsWriter(METRICS_TOPIC_NAME);

        kafkaEventsWriter.send(null, metricValue);
    }
}
