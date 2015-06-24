package fortscale.streaming.service.aggregation;

import java.util.List;

public interface FeatureBucketsStore {
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime);
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId);
}
