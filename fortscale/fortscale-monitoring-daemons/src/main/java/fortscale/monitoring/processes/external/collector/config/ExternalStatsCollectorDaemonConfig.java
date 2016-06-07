package fortscale.monitoring.processes.external.collector.config;

import fortscale.monitoring.external.stats.collector.impl.linux.config.LinuxCollectorsServicesImplConfig;
import fortscale.monitoring.external.stats.collector.impl.mongo.collection.config.MongoCollectionCollectorImplConfig;
import fortscale.monitoring.external.stats.collector.impl.mongo.db.config.MongoDBCollectorImplServiceConfig;
import fortscale.monitoring.external.stats.collector.impl.mongo.server.config.MongoServerCollectorImplServiceConfig;
import fortscale.monitoring.processes.group.config.MonitoringProcessGroupCommonConfig;
import fortscale.utils.spring.PropertySourceConfigurer;
import fortscale.utils.spring.StandardProcessPropertiesConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;


@Configuration
@Import({MonitoringProcessGroupCommonConfig.class,
        LinuxCollectorsServicesImplConfig.class,
        MongoCollectionCollectorImplConfig.class,
        MongoDBCollectorImplServiceConfig.class,
        MongoServerCollectorImplServiceConfig.class
})
public class ExternalStatsCollectorDaemonConfig {
    @Bean
    private static PropertySourceConfigurer ExternalStatsCollectorDaemonEnvironmentPropertyConfigurer() {
        Properties properties = ExternalStatsCollectorDaemonProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(ExternalStatsCollectorDaemonProperties.class, properties);

        return configurer;
    }

    @Bean
    public static StandardProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {

        StandardProcessPropertiesConfigurer configurer= new StandardProcessPropertiesConfigurer();

        return configurer;
    }
}
