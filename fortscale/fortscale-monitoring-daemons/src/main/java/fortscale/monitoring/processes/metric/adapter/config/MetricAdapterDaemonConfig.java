package fortscale.monitoring.processes.metric.adapter.config;

import fortscale.monitoring.metrics.adapter.config.MetricAdapterServiceConfig;
import fortscale.monitoring.processes.group.config.MonitoringProcessGroupCommonConfig;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;


@Configuration
@Import({MonitoringProcessGroupCommonConfig.class, MetricAdapterServiceConfig.class})
public class MetricAdapterDaemonConfig {
    @Bean
    private static PropertySourceConfigurer monitoringDaemonEnvironmentPropertyConfigurer() {
        Properties properties = MetricAdapterDaemonProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MetricAdapterDaemonProperties.class, properties);

        return configurer;
    }


}
