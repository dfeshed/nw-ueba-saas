package fortscale.monitoring.metricAdapter.config;


import fortscale.global.configuration.GlobalConfiguration;
import fortscale.monitoring.metricAdapter.MetricAdapter;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterStats;
import fortscale.utils.influxdb.config.InfluxdbClientConfig;
import fortscale.utils.kafka.KafkaTopicSyncReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:META-INF/montoring-metric-adapter.properties")
@Import({InfluxdbClientConfig.class, GlobalConfiguration.class})
public class MetricAdapterConfig {

    @Value("${metricadapter.kafka.topic}")
    private String topicName;
    @Value("${metricadapter.kafka.metrics.clientid}")
    private String topicClientId;
    @Value("${metricadapter.kafka.metrics.partition}")
    private int topicPartition;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public KafkaTopicSyncReader kafkaTopicSyncReader() {
        return new KafkaTopicSyncReader(topicClientId, topicName, topicPartition);
    }

    @Bean
    public MetricAdapterStats metricAdapterStats() {
        return new MetricAdapterStats();
    }

    @Bean
    MetricAdapter metricAdapter() {
        return new MetricAdapter();
    }
}
