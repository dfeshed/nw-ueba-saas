package fortscale.aggregation.domain.feature.event;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service")
public class FeatureBucketAggrSendingQueueRepositoryMetrics extends StatsMetricsGroup {
    public FeatureBucketAggrSendingQueueRepositoryMetrics(StatsService statsService) {
        super(statsService, FeatureBucketAggrSendingQueueRepository.class,
                new StatsMetricsGroupAttributes());
    }

    @StatsDateMetricParams(name="fireTime")
    public long fireEpochtime;
}
