package fortscale.services.monitoring.stats;

/**
 * Created by gaashh on 4/3/16.
 */

// TODO: make it a singleton via Spring

public interface StatsService {

    public StatsMetricsGroupHandler registerStatsMetricsGroup(StatsMetricsGroup metricsGroup);

}
