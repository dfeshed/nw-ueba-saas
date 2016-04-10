package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.services.monitoring.stats.StatsService;
import fortscale.services.monitoring.stats.engine.StatsEngine;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaashh on 4/3/16.
 */

// TODO: add @Service("sshUsersWhitelist") for spring


public class StatsServiceImpl implements StatsService {

    StatsEngine statsEngine;
    List<StatsMetricsGroupHandler> metricsGroupHandlersList;

    // ctor
    public StatsServiceImpl() {
        metricsGroupHandlersList = new LinkedList<>();
    }

    // Register metricGroup to the stats service
    // 1. Creates an group handler for the statsGroup and bind it to this service
    // 2. Add the group handler to the group handler list
    // TODO: multithreading !!!
    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup) {

        // Creates an handler for the statsGroup and bind it to this service
        StatsMetricsGroupHandlerImpl groupHandler = new StatsMetricsGroupHandlerImpl(metricsGroup, this);

        //  Add the group handler to the group handler list
        metricsGroupHandlersList.add(groupHandler);

        return groupHandler;
    }

    public void registerStatsEngine(StatsEngine statsEngine) {
        // TODO: check if stats engine is already registered
        this.statsEngine = statsEngine;
    }

    public void writeMetricsGroupsToEngine(long epochTime){
        // TODO: multithreading !!!
        for (StatsMetricsGroupHandler metricsGroupHandler : metricsGroupHandlersList) {
            metricsGroupHandler.writeMetricGroupsToEngine(epochTime);
        }
    }


    // --- getters / setters

    public StatsEngine getStatsEngine() {
        return statsEngine;
    }

}
