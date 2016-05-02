package fortscale.utils.kafka.kafkaMetricsTopicSyncReader.config;

import fortscale.utils.kafka.kafkaMetricsTopicSyncReader.KafkaMetricsTopicSyncReader;
import fortscale.utils.kafka.kafkaTopicSyncReader.config.KafkaTopicSyncReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KafkaTopicSyncReaderConfig.class})
public class KafkaMetricsTopicSyncReaderConfig {
    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.kafka.so.timeout}") // socket timeout in milliseconds
    private int soTimeout;
    @Value("${fortscale.kafka.buffer.size}")
    private int bufferSize;
    @Value("${fortscale.kafka.fetch.size}")
    private int fetchSize;

    @Bean
    public KafkaMetricsTopicSyncReader kafkaMetricsTopicSyncReader() {
        return new KafkaMetricsTopicSyncReader(fetchSize, bufferSize, soTimeout, hostAndPort);
    }
}
