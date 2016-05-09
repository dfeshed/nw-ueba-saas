package fortscale.monitoring.samza.topicReader.config;

import fortscale.monitoring.metricAdapter.config.MetricAdapterProperties;
import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SamzaMetricsTopicSyncReaderConfig {
    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${samza.topic.metrics.consumer.sockettimeout.milliseconds}")
    private int soTimeout;
    @Value("${samza.topic.metrics.consumer.buffersize}")
    private int bufferSize;
    @Value("${samza.topic.metrics.consumer.fetchsize}")
    private int fetchSize;
    @Value("${samza.topic.metrics.consumer.clientid}")
    private String clientId;
    @Value("${samza.topic.metrics.consumer.topic.name}")
    private String topicName;
    @Value("${samza.topic.metrics.consumer.partition}")
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
