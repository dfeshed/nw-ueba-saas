package fortscale.utils.monitoring.stats.config;

import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.monitoring.stats.engine.StatsEngine;
import fortscale.utils.monitoring.stats.engine.topic.StatsTopicEngine;
import fortscale.utils.monitoring.stats.engine.topic.config.StatsTopicEngineConfig;
import fortscale.utils.monitoring.stats.engine.topic.config.StatsTopicEngineProperties;
import fortscale.utils.monitoring.stats.impl.StatsServiceImpl;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * Created by gaashh on 4/25/16.
 */

@Configuration
@Import(StatsTopicEngineConfig.class)
public class StandardStatsServiceConfig {

    @Autowired
    @Qualifier("statsTopicEngine")
    StatsTopicEngine statsEngine;

    /**
     *
     * StatsServiceProperties property object configurer bean
     *
     * @return
     */
    @Bean
    private static PropertySourceConfigurer propertyConfigurer() {

        // Get the properties object
        Properties properties = StatsServiceProperties.getProperties();

        // Create a configurer bean
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(StandardStatsServiceConfig.class, properties);

        return configurer;
    }


    @Bean
    public StatsServiceImpl standardStatsService() {
        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine);
        return statsService;
    }




}
