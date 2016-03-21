package fortscale.monitoring.writer;

import fortscale.utils.kafka.KafkaEventsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gils
 * 20/03/2016
 */
public class FortscaleMonitoringWriter {
    private static Logger logger = LoggerFactory.getLogger(FortscaleMonitoringWriter.class);

    private static final String METRIC_TOPIC_NAME = "kafka.metrics";

    public void writeMetric(String metricValue) {
        KafkaEventsWriter kafkaEventsWriter = new KafkaEventsWriter(METRIC_TOPIC_NAME);

        kafkaEventsWriter.send("key", metricValue);
    }
}
