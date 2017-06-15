package fortscale.aggregation.feature.bucket.strategy;

import java.util.List;
import java.util.Map;

public interface FeatureBucketStrategyStore {
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime);
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyDataContainsEventTime(String strategyEventContextId, long eventTime);
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime, Map<String, String> contextMap);
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData);
}
