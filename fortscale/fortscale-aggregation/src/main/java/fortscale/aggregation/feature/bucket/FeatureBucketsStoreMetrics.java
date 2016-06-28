package fortscale.aggregation.feature.bucket;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service")
public class FeatureBucketsStoreMetrics extends StatsMetricsGroup {
    public FeatureBucketsStoreMetrics(StatsService statsService,
                                      String storeType,
                                      FeatureBucketConf featureBucketConf) {
        super(statsService, FeatureBucketsService.class, new StatsMetricsGroupAttributes() {{
            addTag("storeType", storeType);
            addTag("bucketName", featureBucketConf.getName());
        }});
    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrieveContextsCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrievedContexts;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrieveFeatureBucketsCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrievedFeatureBuckets;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrieveFeatureBucketsFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long saveFeatureBucketsCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long saveFeatureBucketsFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long insertFeatureBucketsCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long insertFeatureBucketsFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateFeatureBucketsCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updatedFeatureBuckets;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateFeatureBucketsFailures;

    @StatsDoubleMetricParams()
    public long collectionCreations;
}
