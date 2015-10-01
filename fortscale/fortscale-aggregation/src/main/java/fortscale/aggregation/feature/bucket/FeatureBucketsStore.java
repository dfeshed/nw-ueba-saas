package fortscale.aggregation.feature.bucket;

import java.util.List;

public interface FeatureBucketsStore {
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime);
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId);
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception;
	public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime);
}
