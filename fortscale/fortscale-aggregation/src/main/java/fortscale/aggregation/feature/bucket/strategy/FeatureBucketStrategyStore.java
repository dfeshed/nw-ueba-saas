package fortscale.aggregation.feature.bucket.strategy;

public interface FeatureBucketStrategyStore {
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime);
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData);
}
