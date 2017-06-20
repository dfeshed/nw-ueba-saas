package fortscale.monitoring.metrics.adapter.topicReader.config;

import fortscale.monitoring.metrics.adapter.topicReader.EngineDataTopicSyncReader;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class EngineDataTopicSyncReaderConfig {
    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.topic.metrics.engineData.consumer.sockettimeout.milliseconds}")
    private int soTimeout;
    @Value("${fortscale.topic.metrics.engineData.consumer.buffersize}")
    private int bufferSize;
    @Value("${fortscale.topic.metrics.engineData.consumer.fetchsize}")
    private int fetchSize;
    @Value("${fortscale.topic.metrics.engineData.consumer.clientid}")
    private String clientId;
    @Value("${fortscale.topic.metrics.engineData.consumer.topic.name}")
    private String topicName;
    @Value("${fortscale.topic.metrics.engineData.consumer.partition}")
    private int partition;


    @Bean
    private static PropertySourceConfigurer EngineDataTopicSyncReaderEnvironmentPropertyConfigurer() {
        Properties properties = EngineDataTopicSyncReaderProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(EngineDataTopicSyncReaderConfig.class, properties);

        return configurer;
    }

    @Bean (destroyMethod = "close")
    public EngineDataTopicSyncReader fortscaleMetricsTopicSyncReader() {

        return new EngineDataTopicSyncReader(fetchSize, bufferSize, soTimeout, hostAndPort, clientId, topicName, partition);
    }
}
