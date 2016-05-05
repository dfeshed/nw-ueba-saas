package fortscale.monitoring.grafana.init.config;


import fortscale.monitoring.config.MonitoringProcessGroupCommonConfig;
import fortscale.monitoring.grafana.init.GrafanaInit;
import fortscale.monitoring.metricAdapter.MetricAdapter;
import fortscale.monitoring.metricAdapter.config.MetricAdapterProperties;
import fortscale.monitoring.metricAdapter.stats.MetricAdapterStats;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.influxdb.config.InfluxdbClientConfig;
import fortscale.utils.kafka.kafkaMetricsTopicSyncReader.KafkaMetricsTopicSyncReader;
import fortscale.utils.kafka.kafkaMetricsTopicSyncReader.config.KafkaMetricsTopicSyncReaderConfig;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

@Configuration
public class GrafanaInitConfig {

    @Value("${grafana.db.source.file.path}")
    private String dbSourceFilePath;
    @Value("${grafana.db.destination.file.path}")
    private String dbDestinationFilePath;

    @Bean
    GrafanaInit grafanaInit() {
        return new GrafanaInit(dbSourceFilePath, dbDestinationFilePath);
    }

    @Bean
    private static PropertySourceConfigurer grafanaInitEnvironmentPropertyConfigurer() {
        Properties properties = GrafanaInitProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(GrafanaInitConfig.class, properties);

        return configurer;
    }


}
