package fortscale.monitoring.process.metric.adapter.config;

import fortscale.monitoring.metrics.adapter.config.MetricAdapterConfig;
import fortscale.monitoring.process.group.config.MonitoringProcessGroupCommonConfig;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;


@Configuration
@Import({MonitoringProcessGroupCommonConfig.class, MetricAdapterConfig.class})
public class MetricAdapterDaemonConfig {
    @Bean
    private static PropertySourceConfigurer monitoringDaemonEnvironmentPropertyConfigurer() {
        Properties properties = MetricAdapterDaemonProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MetricAdapterDaemonProperties.class, properties);

        return configurer;
    }
}
