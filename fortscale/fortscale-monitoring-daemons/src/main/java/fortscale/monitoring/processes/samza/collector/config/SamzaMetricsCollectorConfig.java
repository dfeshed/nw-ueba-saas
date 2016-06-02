package fortscale.monitoring.processes.samza.collector.config;

import fortscale.monitoring.processes.group.config.MonitoringProcessGroupCommonConfig;
import fortscale.monitoring.external.stats.samza.collector.service.config.SamzaMetricsCollectorServiceConfig;
import fortscale.utils.spring.StandardProcessPropertiesConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({MonitoringProcessGroupCommonConfig.class, SamzaMetricsCollectorServiceConfig.class})
public class SamzaMetricsCollectorConfig {

    @Bean
    public static StandardProcessPropertiesConfigurer samzaMetricsCollectorMainProcessPropertiesConfigurer() {

        StandardProcessPropertiesConfigurer configurer= new StandardProcessPropertiesConfigurer();


        return configurer;
    }
}
