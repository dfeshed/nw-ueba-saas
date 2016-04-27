package fortscale.utils.kafka.kafkaTopicSyncReader.config;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.utils.kafka.kafkaTopicSyncReader.KafkaTopicSyncReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:META-INF/kafka/kafkaTopicSyncReader/KafkaTopicSyncReader/config/kafkaTopicSyncReader.properties")
@Import({GlobalConfiguration.class})
public class KafkaTopicSyncReaderConfig {
    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.kafka.so.timeout}")
    private int soTimeout;
    @Value("${fortscale.kafka.buffer.size}")
    private int bufferSize;
    @Value("${fortscale.kafka.fetch.size}")
    private int fetchSize;

    @Bean
    KafkaTopicSyncReader kafkaTopicSyncReader() {
        return new KafkaTopicSyncReader(fetchSize, bufferSize, soTimeout, hostAndPort);
    }
}
