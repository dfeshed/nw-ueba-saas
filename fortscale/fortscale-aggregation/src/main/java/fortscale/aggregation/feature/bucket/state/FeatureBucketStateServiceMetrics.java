package fortscale.aggregation.feature.bucket.state;

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
    @StatsLongMetricParams
    public long updateFeatureBucketStateSuccess;

    @StatsLongMetricParams
    public long updateFeatureBucketStateFailure;

    @StatsLongMetricParams
    public long getFeatureBucketStateSuccess;

    @StatsLongMetricParams
    public long getFeatureBucketStateReturnedNull;

    @StatsDateMetricParams(name="lastSyncedEventDate")
    public long lastSyncedEventDate;

    @StatsDateMetricParams(name="lastClosedDailyBucketDate")
    public long lastClosedDailyBucketDate;
}
