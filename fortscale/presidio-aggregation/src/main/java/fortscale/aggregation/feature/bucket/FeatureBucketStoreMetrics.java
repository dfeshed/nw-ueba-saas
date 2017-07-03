package fortscale.aggregation.feature.bucket;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "aggregation.service.feature-bucket-store")
public class FeatureBucketStoreMetrics extends StatsMetricsGroup {
	public FeatureBucketStoreMetrics(
			StatsService statsService, String storeImpl, String featureBucketConfName) {

		super(statsService, FeatureBucketStoreMongoImpl.class, new StatsMetricsGroupAttributes() {
			{
				addTag("store", storeImpl);
				addTag("featureBucketConf", featureBucketConfName);
			}
		});
	}

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long bulkWritesNotAcknowledged;

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

	@StatsDoubleMetricParams(rateSeconds = 1)
	public long readFeatureBucketFailers;

	@StatsDoubleMetricParams()
	public long collectionCreations;
}
