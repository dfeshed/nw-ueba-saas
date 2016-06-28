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
    public long retrieveCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrievedDocuments;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long retrieveFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long saveCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long saveFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long insertCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long insertFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateCalls;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updatedDocuments;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long updateFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long collectionCreations;
}
