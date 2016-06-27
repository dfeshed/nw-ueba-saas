package fortscale.aggregation.feature.event.metrics;

import fortscale.aggregation.feature.event.IAggrFeatureEventService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service")
public class AggrFeatureEventMetrics extends StatsMetricsGroup {
    public AggrFeatureEventMetrics(StatsService statsService) {
        super(statsService, IAggrFeatureEventService.class, new StatsMetricsGroupAttributes());
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long invalidFeatureBucketConfNames;

    @StatsDateMetricParams(name="fireTime")
    public long fireTimeEpoch;
}
