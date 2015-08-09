package fortscale.aggregation.feature.bucket;

import java.util.List;

public interface FeatureBucketsStore {
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime);
	public List<FeatureBucket> getFeatureBuckets(FeatureBucketConf featureBucketConf, String entityType, String entityName, String feature, Long startTime, Long endTime);
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId);
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket);
}
