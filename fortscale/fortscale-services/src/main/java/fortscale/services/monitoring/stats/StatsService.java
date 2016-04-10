package fortscale.services.monitoring.stats;

import fortscale.services.monitoring.stats.engine.StatsEngine;

/**
 * Created by gaashh on 4/3/16.
 */

// TODO: make it a singleton via Spring

public interface StatsService {

    public void writeMetricsGroupsToEngine(long epochTime);

    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup);

    public void registerStatsEngine(StatsEngine statsEngine);

    public StatsEngine getStatsEngine();
}
