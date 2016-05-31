package fortscale.utils.monitoring.stats.config;

import fortscale.utils.monitoring.stats.engine.topic.StatsTopicEngine;
import fortscale.utils.monitoring.stats.engine.topic.config.StatsTopicEngineConfig;
import fortscale.utils.monitoring.stats.impl.StatsServiceImpl;
import fortscale.utils.process.hostnameService.HostnameService;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
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
 * Standard stats service configuration class. It should be used for non-samza process. The process must have topic access.
 *
 * The class creates a stats service hooked to stats topic engine
 *
 * Created by gaashh on 4/25/16.
 */

@Configuration
@Import( { StatsTopicEngineConfig.class, HostnameServiceConfig.class } )
public class StandardStatsServiceConfig {

    @Autowired
    @Qualifier("statsTopicEngine")
    StatsTopicEngine statsEngine;

    @Autowired
    HostnameService hostnameService;

    @Value("${fortscale.process.name:UNKNOWN-PROCESS-NAME}")
    String processName;

    @Value("${fortscale.process.group.name:UNKNOWN-PROCESS-GROUP-NAME}")
    String processGroupName;

    @Value("${fortscale.process.pid:0}")
    long processPID;

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

    @Value("${fortscale.monitoring.stats.service.disable}")
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
     * The main bean function, create the stats service and hook the engine to it
     *
     * @return
     */
    @Bean
    public StatsServiceImpl standardStatsService() {

        // Disabled?
        if (disable != 0) {
            return null;
        }

        // Create it

        boolean isExternalMetricUpdateTick = false;
        boolean isExternalEnginePushTick = false;

        StatsServiceImpl statsService = new StatsServiceImpl(statsEngine,
                processName, processGroupName, processPID, hostnameService,
                tickSeconds,
                metricsUpdatePeriodSeconds, metricsUpdateSlipWarnSeconds,
                enginePushPeriodSeconds, enginePushSlipWarnSeconds,
                isExternalMetricUpdateTick, isExternalEnginePushTick);

        return statsService;

    }

}
