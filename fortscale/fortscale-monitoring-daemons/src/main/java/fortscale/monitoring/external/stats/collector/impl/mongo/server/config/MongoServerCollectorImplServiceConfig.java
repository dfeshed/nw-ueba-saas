package fortscale.monitoring.external.stats.collector.impl.mongo.server.config;


import fortscale.monitoring.external.stats.collector.impl.mongo.config.MongodbConfig;
import fortscale.monitoring.external.stats.collector.impl.mongo.server.MongoServerCollectorImplService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Properties;


@Configuration
@Import(MongodbConfig.class)
public class MongoServerCollectorImplServiceConfig {

    @Value("${fortscale.external.collectors.mongo.server.disabled}")
    boolean collectorDisabled;

    @Value("${fortscale.external.collectors.mongo.server.tick.seconds}")
    long collectorTickPeriodSeconds;

    @Value("${fortscale.external.collectors.mongo.server.slip.warn.seconds}")
    long collectorTickSlipWarnSeconds;

    @Autowired
    StatsService statsService;
    @Autowired
    @Qualifier("externalStatsMonitoringCollectorMongoTemplate")
    MongoTemplate mongoTemplate;

    @Bean
    public MongoServerCollectorImplService mongoServerCollectorImplService()
    {
        if (collectorDisabled)
        {
            return null;
        }

        return new MongoServerCollectorImplService(statsService,true,collectorTickPeriodSeconds,collectorTickSlipWarnSeconds,mongoTemplate);
    }

    @Bean
    private static PropertySourceConfigurer MongoServerCollectorImplPropertyConfigurer() {

        // Get the properties object
        Properties properties = MongoServerCollectorImplServiceProperties.getProperties();

        // Create a configurer bean
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MongoServerCollectorImplService.class, properties);

        return configurer;
    }
}
