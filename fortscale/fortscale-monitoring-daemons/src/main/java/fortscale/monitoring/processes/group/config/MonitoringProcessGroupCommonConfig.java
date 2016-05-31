package fortscale.monitoring.processes.group.config;

import fortscale.monitoring.processes.metric.adapter.config.MetricAdapterDaemonProperties;
import fortscale.utils.process.standardProcess.config.StandardProcessBaseConfig;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;


@Configuration
@Import({StandardProcessBaseConfig.class})
public class MonitoringProcessGroupCommonConfig {

    @Bean
    private static PropertySourceConfigurer MonitoringProcessGroupCommonEnvironmentPropertyConfigurer() {
        Properties properties = MonitoringProcessGroupCommonProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MonitoringProcessGroupCommonProperties.class, properties);

        return configurer;
    }


}
