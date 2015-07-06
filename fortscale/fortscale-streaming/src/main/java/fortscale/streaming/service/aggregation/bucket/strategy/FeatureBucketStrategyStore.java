package fortscale.streaming.service.aggregation.bucket.strategy;

public interface FeatureBucketStrategyStore {
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime);
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData);
}
