package fortscale.utils.monitoring.stats;

import fortscale.utils.monitoring.stats.engine.StatsEngine;

/**
 * Created by gaashh on 4/3/16.
 */

// TODO: make it a singleton via Spring

/**
 * StatsService periodically collects metrics from the application and stores them in time series data base.
 *
 * To add application metrics, build a new class that extends StatsMetricsGroup and mark the fields with annotations.
 *
 * The service writes the metrics to an engine. The engine should be registered to the service at the processes
 * initialization phase, before any application metrics are created. Note however, mainly for unit tests, the stats
 * service initially use the built-in null stats service. This enable test to register metrics groups without providing
 * a real engine. Still, if a metric group is registered, the engine is locked and cannot be changed.
 *
 * StatsService is one instance per process (aka singleton).
 *
 * Note: typically the application does not use this service directly. It uses it indirectly via StatsMetricsGroup.
 *
 */
public interface StatsService {


    /**
     * Collect all the registered application metrics and writes them to the engine.
     *
     * This is an internal method and should not be called by the application (except for testing)
     *
     * @param epochTime metrics epoch time. Zero indicates now
     */
    void writeMetricsGroupsToEngine(long epochTime);

    /**
     *
     * Registers a metrics group object, an application metrics group object to the services.
     *
     * This function is called from the merticsGroup ctor. IT SHOULD NOT BE CALLED FROM APPLICATION CODE!
     *
     * @param metricsGroup
     * @return metricsGroupHandler. This enabled StatsMetricsGroup to hook to its handler
     */
    StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup);


    // --- getters/setters
    StatsEngine getStatsEngine();
}
