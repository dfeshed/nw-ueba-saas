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

    // Tag names of tags that are added automatically
    String PROCESS_NAME_TAG_NAME       = "process";
    String PROCESS_GROUP_NAME_TAG_NAME = "processGroup";
    String HOSTNAME_TAG_NAME = "host";

    /**
     * This function is called by external thread to update all (except manually updated) metrics groups periodically
     *
     * Calling this function requires isExternalMetricUpdateTick ctor arg to be true.
     *
     * This function is used only in special cases like Samza task
     *
     * The function does not throw excpetion. In case of internal problem, the exception is just logged
     *
     * @param epochTime metrics epoch time. Zero indicates now
     */
    void externalMetricsUpdateTick(long epochTime);

    /**
     * Pushes(writes) the date pending in the engine to the destination
     *
     * Typically, the application should not call this function because the engine pushes the data itself.
     *
     * However, if the application disables periodic flushes, it should call this functions once in a while (e.g. 1 minute)
     *
     * The function does not throw exceptions. If there was a problem, an error is logged and function returns normally
     *
     * This is an internal method and should not be called by the application (except for testing)
     *
     */
    void manualUpdatePush();

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

    /**
     *
     * tick function should be called periodically to update stats and to write them to the engine
     *
     * @param epoch - time when tick occurred. This epoch as parameter enables easy testing
     */
    void tick(long epoch);

    // --- getters/setters
    StatsEngine getStatsEngine();
}
