package fortscale.aggregation;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service.timer")
public class DataSourcesSyncTimerMetrics extends StatsMetricsGroup {
    public DataSourcesSyncTimerMetrics(StatsService statsService) {
        super(statsService, DataSourcesSyncTimer.class, new StatsMetricsGroupAttributes());
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processed;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long nullEpochtime;

    @StatsDoubleMetricParams()
    public long listeners;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long listenerUpdateFailures;

    @StatsDateMetricParams(name="lastEventTime")
    public long lastEventEpochtime;
}
