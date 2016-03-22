package fortscale.monitoring.writer;

import fortscale.utils.kafka.KafkaEventsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Monitoring metrics writer
 *
 * @author gils
 * 20/03/2016
 */
@Service
public class MonitoringMetricsWriter {
    private static Logger logger = LoggerFactory.getLogger(MonitoringMetricsWriter.class);

    private KafkaEventsWriter kafkaEventsWriter;

    public void writeMetric(String metricValue) {
        kafkaEventsWriter.send(null, metricValue);
    }

    public void setKafkaEventsWriter(KafkaEventsWriter kafkaEventsWriter) {
        this.kafkaEventsWriter = kafkaEventsWriter;
    }
}
