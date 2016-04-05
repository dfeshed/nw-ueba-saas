package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.services.monitoring.stats.StatsService;

/**
 * Created by gaashh on 4/3/16.
 */

// TODO: add @Service("sshUsersWhitelist") for spring


public class StatsServiceImpl implements StatsService {

    // ctor
    // 1. Creates an handler for the statsGroup and bind it to this service
    // 2.
    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup) {

        // Creates an handler for the statsGroup and bind it to this service
        StatsMetricsGroupHandlerImpl groupHandler = new StatsMetricsGroupHandlerImpl(metricsGroup, this);

        return groupHandler;
    }

/*
    public StatsMetricsGroupHandler registerStatsMetricsGroup(String groupName,
                                                              StatsMetricsGroupParams metricsGroup,
                                                              Class<?> instrumentedClass ) {

        StatsMetricsGroupHandlerImpl groupHandler = new StatsMetricsGroupHandlerImpl(groupName, metricsGroup, instrumentedClass, this);

        return groupHandler;
    }
*/
}
