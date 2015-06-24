package fortscale.streaming.service.aggregation;

import java.util.List;

public interface FeatureBucketsStore {
	// TODO implement API
	public List<FeatureBucket> getFeatureBuckets();
	public void saveFeatureBucket();
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime);
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String strategyId);
}
