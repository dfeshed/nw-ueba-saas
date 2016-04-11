package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.services.monitoring.stats.StatsService;
import fortscale.services.monitoring.stats.engine.StatsEngine;

import java.util.LinkedList;
import java.util.List;

// TODO: add @Service("sshUsersWhitelist") for spring


/**
 * StatsService main implementation. See StatService for common documentation.
 *
 * It has 2 main functions:
 *   1. Register the metrics groups
 *   2. Sample the metrics groups and writes the metrics values to the engine
 *
 * There shall be one instance of this class per process.
 *
 * Created by gaashh on 4/3/16.
 */

public class StatsServiceImpl implements StatsService {

    // The service stats engine. It must be set before any metricsGroup is registered.
    // TODO: verify engine is set before any class is registered
    StatsEngine statsEngine;

    // A list of registered metrics group handlers. Note the metrics group handler holds the metrics group.
    List<StatsMetricsGroupHandler> metricsGroupHandlersList;

    /**
     * ctor
     */
    public StatsServiceImpl() {
        metricsGroupHandlersList = new LinkedList<>();
    }

    /**
     *  Register StatsMetricsGroup object to the stats service.
     *
     *  The registration has two steps/
     *    1. Creates an group handler for the statsGroup and bind it to this service
     *    2. Add the group handler to the service metrics group handler list
     *
     * @param metricsGroup - the metrics group to register
     * @return - metrics group handler (it is saved in the metrics group for back reference)
     */
    // TODO: multithreading !!!
    // TODO: check engine is present
    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup) {

        // Creates an handler for the statsGroup and bind it to this service
        StatsMetricsGroupHandlerImpl groupHandler = new StatsMetricsGroupHandlerImpl(metricsGroup, this);

        //  Add the group handler to the group handler list
        metricsGroupHandlersList.add(groupHandler);

        return groupHandler;
    }

    /**
     * Register a StatsEngine to the service.
     *
     * The engine is registered once and must be set before any metrics is registered
     *
     * @param statsEngine - the engine to register
     */
    public void registerStatsEngine(StatsEngine statsEngine) {
        // TODO: check if stats engine is already registered
        this.statsEngine = statsEngine;
    }

    /**
     * Collect all the registered application metrics and writes them to the engine by calling all the metrics groups
     * handlers
     *
     * This is an internal method and should not be called by the application (except for testing)
     *
     * @param epochTime metrics epoch time. Zero indicates now
     */
    // TODO: multithreading !!!
    public void writeMetricsGroupsToEngine(long epochTime){

        // Loop all the metrics groups handlers and ask them to collect and write their metrics
        for (StatsMetricsGroupHandler metricsGroupHandler : metricsGroupHandlersList) {
            metricsGroupHandler.writeMetricGroupsToEngine(epochTime);
        }
    }

    // --- getters / setters

    public StatsEngine getStatsEngine() {
        return statsEngine;
    }

}
