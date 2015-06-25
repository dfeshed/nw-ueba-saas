package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;

public interface FeatureBucketStrategyFactorySamza extends FeatureBucketStrategyFactory{
	public void setStrategyStore(FeatureBucketStrategyStore featureBucketStrategyStore);
}
