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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 *
 * Standard stats service configuration class. It should be used for non-samza process. The process must have topic access.
 *
 * The class creates a stats service hooked to stats topic engine
 *
 * Created by gaashh on 4/25/16.
 */

@Configuration
@Import(StatsTopicEngineConfig.class)
public class StandardStatsServiceConfig {

    @Autowired
    @Qualifier("statsTopicEngine")
    StatsTopicEngine statsEngine;

    @Value("${fortscale.monitoring.stats.service.tick.seconds}")
    long tickSeconds;

    @Value("${fortscale.monitoring.stats.service.periodicMetricsUpdate.seconds}")
    long metricsUpdatePeriodSeconds;

    @Value("${fortscale.monitoring.stats.service.periodicMetricsUpdate.slip}")
    long metricsUpdateSlipWarnSeconds;

    @Value("${fortscale.monitoring.stats.service.enginePush.seconds}")
    long enginePushPeriodSeconds;

    @Value("${fortscale.monitoring.stats.service.enginePush.slip}")
    long enginePushSlipWarnSeconds;

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


    /**
     *
     * The main bean function, create the stats service and hook the engine to it
     *
     * @return
     */
    @Bean
    public StatsServiceImpl standardStatsService() {
        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine, tickSeconds,
                                                             metricsUpdatePeriodSeconds, metricsUpdateSlipWarnSeconds,
                                                             enginePushPeriodSeconds, enginePushSlipWarnSeconds);
        return statsService;
    }




}
