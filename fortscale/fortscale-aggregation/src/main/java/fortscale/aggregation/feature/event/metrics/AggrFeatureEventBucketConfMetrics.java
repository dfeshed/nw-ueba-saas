package fortscale.aggregation.feature.event.metrics;

import fortscale.aggregation.feature.event.IAggrFeatureEventService;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service.aggr-feature-events.bucket-conf")
public class AggrFeatureEventBucketConfMetrics extends StatsMetricsGroup {
    public AggrFeatureEventBucketConfMetrics(StatsService statsService, String bucketConfName) {
        super(statsService, IAggrFeatureEventService.class, new StatsMetricsGroupAttributes() {{
            addTag("bucketConfName", bucketConfName);
        }});
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long featureBucketAggrMetadaSaves;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long bucketsNotFound;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long sentToQueue;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long Fs;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long Ps;
}
