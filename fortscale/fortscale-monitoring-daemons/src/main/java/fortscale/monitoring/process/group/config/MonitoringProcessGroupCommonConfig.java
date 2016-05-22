package fortscale.monitoring.process.group.config;

import fortscale.utils.spring.PropertySourceConfigurer;
import fortscale.utils.process.standardProcess.config.StandardProcessBaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

//import fortscale.utils.kafka.KafkaTopicSyncReader;

/**
 * Created by baraks on 4/25/2016.
 */
@Configuration
@Import({StandardProcessBaseConfig.class})
public class MonitoringProcessGroupCommonConfig {
    @Bean
    private static PropertySourceConfigurer MonitoringProcessGroupEnvironmentPropertyConfigurer() {
        Properties properties = MonitoringProcessGroupCommonProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MonitoringProcessGroupCommonProperties.class, properties);

        return configurer;
    }
}
