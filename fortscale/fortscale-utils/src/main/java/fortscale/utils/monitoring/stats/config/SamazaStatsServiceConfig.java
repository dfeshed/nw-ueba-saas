package fortscale.utils.monitoring.stats.config;

import fortscale.utils.monitoring.stats.engine.topic.StatsTopicEngine;
import fortscale.utils.monitoring.stats.engine.topic.config.StatsTopicEngineConfig;
import fortscale.utils.monitoring.stats.impl.StatsServiceImpl;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


import java.util.Properties;

/**
 *
 * Samza stats service configuration class. It should be used for samza process. The process must have topic access.
 *
 * The Samza stats service is similar to the standard stats service with the following difference:
 *   1. Metrics are updated using Samaza metric infra by calling TODO
 *   2. Hence, periodic metrics update is disabled
 *
 * The class creates a stats service hooked to stats topic engine
 *
 * Created by gaashh on 5/25/16.
 */

@Configuration
@Import(StatsTopicEngineConfig.class)
public class SamazaStatsServiceConfig {

    @Autowired
    @Qualifier("statsTopicEngine")
    StatsTopicEngine statsEngine;

    @Value("${fortscale.monitoring.stats.service.tick.seconds}")
    long tickSeconds;

    @Value("${fortscale.monitoring.stats.service.periodicMetricsUpdate.seconds.samza}")
    long metricsUpdatePeriodSeconds;

    @Value("${fortscale.monitoring.stats.service.periodicMetricsUpdate.slip.samza}")
    long metricsUpdateSlipWarnSeconds;

    @Value("${fortscale.monitoring.stats.service.enginePush.seconds}")
    long enginePushPeriodSeconds;

    @Value("${fortscale.monitoring.stats.service.enginePush.slip}")
    long enginePushSlipWarnSeconds;

    @Value("${fortscale.monitoring.stats.service.disable.samza}")
    long disable;

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
     * The main bean function, create the stats service and hook the engine to it, unless disabled
     *
     * @return
     */
    @Bean
    public StatsServiceImpl samzaStatsService() {

        // Disabled?
        if (disable != 0) {
            return null;
        }

        // Create it
        boolean isExternalMetricUpdateTick = true; // metrics are updated by Samza gauge
        boolean isExternalEnginePushTick   = false;

        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine, tickSeconds,
                metricsUpdatePeriodSeconds, metricsUpdateSlipWarnSeconds,
                enginePushPeriodSeconds,    enginePushSlipWarnSeconds,
                isExternalMetricUpdateTick, isExternalEnginePushTick);
        return statsService;
    }




}
