package fortscale.aggregation.feature.bucket.strategy;

import java.util.Map;

public interface FeatureBucketStrategyStore {
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime);
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime, Map<String, String> contextMap);
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData);
}
