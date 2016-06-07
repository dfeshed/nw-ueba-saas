package fortscale.monitoring.external.stats.collector.impl.mongo.db.config;


import fortscale.monitoring.external.stats.collector.impl.mongo.config.MongodbConfig;
import fortscale.monitoring.external.stats.collector.impl.mongo.db.MongoDBCollectorImplService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Properties;


@Configuration
@Import(MongodbConfig.class)
public class MongoDBCollectorImplServiceConfig {

    @Value("${fortscale.external.collectors.mongo.db.disabled}")
    boolean collectorDisabled;

    @Value("${fortscale.external.collectors.mongo.db.tick.seconds}")
    long collectorTickPeriodSeconds;

    @Value("${fortscale.external.collectors.mongo.db.slip.warn.seconds}")
    long collectorTickSlipWarnSeconds;

    @Autowired
    StatsService statsService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public MongoDBCollectorImplService mongoDBCollectorImplService()
    {
        if (collectorDisabled)
        {
            return null;
        }

        return new MongoDBCollectorImplService(statsService,true,collectorTickPeriodSeconds,collectorTickSlipWarnSeconds,mongoTemplate);
    }

    @Bean
    private static PropertySourceConfigurer MongoDBCollectorImplPropertyConfigurer() {

        // Get the properties object
        Properties properties = MongoDBCollectorImplServiceProperties.getProperties();

        // Create a configurer bean
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(MongoDBCollectorImplService.class, properties);

        return configurer;
    }
}
