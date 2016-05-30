package fortscale.monitoring.process.samza.collector.config;

import fortscale.monitoring.process.group.config.MonitoringProcessGroupCommonConfig;
import fortscale.monitoring.external.stats.samza.collector.service.config.SamzaMetricsCollectorServiceConfig;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;


@Configuration
@Import({MonitoringProcessGroupCommonConfig.class, SamzaMetricsCollectorServiceConfig.class})
public class SamzaMetricsCollectorConfig {
    @Bean
    private static PropertySourceConfigurer samzaMetricsCollectorEnvironmentPropertyConfigurer() {
        Properties properties = SamzaMetricsCollectorProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(SamzaMetricsCollectorProperties.class, properties);

        return configurer;
    }
}
