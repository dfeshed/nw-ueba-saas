package fortscale.monitoring.external.stats.samza.collector.topicReader.config;

import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SamzaMetricsTopicSyncReaderConfig {
    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.samzametricscollector.topic.metrics.consumer.sockettimeout.milliseconds}")
    private int soTimeout;
    @Value("${fortscale.samzametricscollector.topic.metrics.consumer.buffersize}")
    private int bufferSize;
    @Value("${fortscale.samzametricscollector.topic.metrics.consumer.fetchsize}")
    private int fetchSize;
    @Value("${fortscale.samzametricscollector.topic.metrics.consumer.clientid}")
    private String clientId;
    @Value("${fortscale.samzametricscollector.topic.metrics.consumer.topic.name}")
    private String topicName;
    @Value("${fortscale.samzametricscollector.topic.metrics.consumer.partition}")
    private int partition;


    @Bean
    private static PropertySourceConfigurer SamzaMetricsTopicSyncReaderEnvironmentPropertyConfigurer() {
        Properties properties = SamzaMetricsTopicSyncReaderProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(SamzaMetricsTopicSyncReaderConfig.class, properties);

        return configurer;
    }

    @Bean (destroyMethod = "close")
    public SamzaMetricsTopicSyncReader kafkaMetricsTopicSyncReader() {

        return new SamzaMetricsTopicSyncReader(fetchSize, bufferSize, soTimeout, hostAndPort, clientId, topicName, partition);
    }
}
