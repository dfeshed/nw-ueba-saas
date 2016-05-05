package fortscale.monitoring.config;

import fortscale.monitoring.MonitoringProcessGroupCommon;
import fortscale.monitoring.metricAdapter.config.MetricAdapterConfig;
import fortscale.monitoring.metricAdapter.config.MetricAdapterProperties;
import fortscale.utils.spring.PropertySourceConfigurer;
import fortscale.utils.standardProcess.config.StandardProcessBaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

//import fortscale.utils.kafka.KafkaTopicSyncReader;

/**
 * Created by baraks on 4/25/2016.
 */
@Configuration
@Import({MonitoringProcessGroupCommonConfig.class, MetricAdapterConfig.class})
public class MonitoringDaemonConfig {
    @Bean
    private static PropertySourceConfigurer monitoringDaemonEnvironmentPropertyConfigurer() {
        Properties properties = MonitoringDaemonProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MonitoringDaemonProperties.class, properties);

        return configurer;
    }
}
