package fortscale.services.monitoring.stats;

/**
 * StatsMetricsGroupHandler handles one StatsMetricsGroup instance. It enable the separation between StatsMetricsGroup
 * and the StatsService implementation. The class instance is created when metrics group is registered to the
 * stats service
 *
 * Created by gaashh on 4/3/16.
 */
public interface StatsMetricsGroupHandler {


    /**
     * Called by StatsMetricsGroup.manualUpdate() to actually perform the actions.
     * The action is to collect the metrics group metrics and to write them to the stats engine.
     * When called without epochTime, the current time is used.
     *
     * This is internal function
     */
    void manualUpdate();

    /**
     * See manualUpdate()
     *
     * @param epochTime Sample time
     */
    void manualUpdate(long epochTime);

    /**
     *
     * Called by the StatsService to sample the metrics group metrics and to write them to the engine. Typically it is
     * call during periodic scan.
     *
     * This is internal function
     *
     * @param epochTime Sample time
     */
    void writeMetricGroupsToEngine(long epochTime);


    // --- Getters/setters

    String getGroupName();

    Class getMetricsGroupInstrumentedClass();

    StatsMetricsGroupAttributes getMetricsGroupAttributes();


}
