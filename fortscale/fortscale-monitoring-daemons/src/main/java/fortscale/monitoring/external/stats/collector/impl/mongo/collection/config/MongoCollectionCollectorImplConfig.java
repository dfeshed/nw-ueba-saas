package fortscale.monitoring.external.stats.collector.impl.mongo.collection.config;


import fortscale.monitoring.external.stats.collector.impl.linux.config.LinuxCollectorsServicesImplProperties;
import fortscale.monitoring.external.stats.collector.impl.mongo.collection.MongoCollectionCollectorImplService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import fortscale.monitoring.external.stats.collector.impl.mongo.config.MongodbConfig;

import java.util.Properties;


@Configuration
@Import(MongodbConfig.class)
public class MongoCollectionCollectorImplConfig {

    @Value("${fortscale.external.collectors.mongo.collection.disabled}")
    boolean collectorDisabled;

    @Value("${fortscale.external.collectors.mongo.collection.tick.seconds}")
    long collectorTickPeriodSeconds;

    @Value("${fortscale.external.collectors.mongo.collection.slip.warn.seconds}")
    long collectorTickSlipWarnSeconds;

    @Autowired
    StatsService statsService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public MongoCollectionCollectorImplService mongoCollectionCollectorImplService()
    {
        if (collectorDisabled)
        {
            return null;
        }

        return new MongoCollectionCollectorImplService(statsService,true,collectorTickPeriodSeconds,collectorTickSlipWarnSeconds,mongoTemplate);
    }

    @Bean
    private static PropertySourceConfigurer MongoCollectionCollectorImplPropertyConfigurer() {

        // Get the properties object
        Properties properties = MongoCollectionCollectorImplServiceProperties.getProperties();

        // Create a configurer bean
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MongoCollectionCollectorImplConfig.class, properties);

        return configurer;
    }
}
