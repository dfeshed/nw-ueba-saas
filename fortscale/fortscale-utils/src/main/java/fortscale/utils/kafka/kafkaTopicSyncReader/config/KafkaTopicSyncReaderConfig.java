package fortscale.utils.kafka.kafkaTopicSyncReader.config;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.utils.kafka.kafkaTopicSyncReader.KafkaTopicSyncReader;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

@Configuration
@Import({GlobalConfiguration.class})
public class KafkaTopicSyncReaderConfig {
    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.kafka.so.timeout}") // socket timeout in milliseconds
    private int soTimeout;
    @Value("${fortscale.kafka.buffer.size}")
    private int bufferSize;
    @Value("${fortscale.kafka.fetch.size}")
    private int fetchSize;

    @Bean
    KafkaTopicSyncReader kafkaTopicSyncReader() {
        return new KafkaTopicSyncReader(fetchSize, bufferSize, soTimeout, hostAndPort);
    }

    @Bean
    private static PropertySourceConfigurer kafkaTopicSyncReaderEnvironmentPropertyConfigurer() {
        Properties properties = KafkaTopicSyncReaderProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(KafkaTopicSyncReaderConfig.class, properties);

        return configurer;
    }
}