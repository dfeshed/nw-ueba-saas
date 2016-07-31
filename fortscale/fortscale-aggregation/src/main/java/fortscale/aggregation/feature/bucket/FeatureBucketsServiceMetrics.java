package fortscale.aggregation.feature.bucket;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service.feature-buckets")
public class FeatureBucketsServiceMetrics extends StatsMetricsGroup {
    public FeatureBucketsServiceMetrics(StatsService statsService, String dataSource) {
        super(statsService, FeatureBucketsService.class, new StatsMetricsGroupAttributes() {{
            addTag("dataSource", dataSource);
        }});
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long nullBucketIds;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long buckets;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long featureBucketUpdates;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long exceptionsUpdatingWithNewEvents;
}
