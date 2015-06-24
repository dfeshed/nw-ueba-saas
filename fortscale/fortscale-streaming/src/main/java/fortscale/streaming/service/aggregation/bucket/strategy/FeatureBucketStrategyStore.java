package fortscale.streaming.service.aggregation.bucket.strategy;


public interface FeatureBucketStrategyStore {
	public void storeStrategyData(FeatureBucketStrategyData featureBucketStrategyData);
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime);
}
