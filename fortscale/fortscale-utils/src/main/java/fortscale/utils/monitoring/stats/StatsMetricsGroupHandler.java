package fortscale.utils.monitoring.stats;

/**
 * StatsMetricsGroupHandler handles one StatsMetricsGroup instance. It enable the separation between StatsMetricsGroup
 * and the StatsService implementation. The class instance is created when metrics group is registered to the
 * stats service
 *
 * Created by gaashh on 4/3/16.
 */
public interface StatsMetricsGroupHandler {


    /**
     * Called from StatsMetricsGroup.manualUpdate() - see its documentation
     *
     * @param epochTime Sample time
     */
    void manualUpdate(long epochTime);

    // --- Getters/setters

    String getGroupName();

    StatsService getStatsService();

    Class getMetricsGroupInstrumentedClass();

    StatsMetricsGroupAttributes getMetricsGroupAttributes();


}
