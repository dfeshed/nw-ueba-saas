package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service.feature-bucket-state-service")
public class FeatureBucketStateServiceMetrics extends StatsMetricsGroup {
    public FeatureBucketStateServiceMetrics(StatsService statsService) {
        super(statsService, FeatureBucketStateServiceMetrics.class, new StatsMetricsGroupAttributes());
    }
    @StatsLongMetricParams(rateSeconds = 1)
    public long updateFeatureBucketStateSuccess;

    @StatsLongMetricParams(rateSeconds = 1)
    public long updateFeatureBucketStateFailure;

    @StatsLongMetricParams(rateSeconds = 1)
    public long getFeatureBucketStateSuccess;

    @StatsLongMetricParams(rateSeconds = 1)
    public long getFeatureBucketStateFailure;

    @StatsDateMetricParams(name="lastSyncedEventDate")
    public long lastSyncedEventDate;

    @StatsDateMetricParams(name="lastClosedDailyBucketDate")
    public long lastClosedDailyBucketDate;
}
