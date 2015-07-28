package fortscale.streaming.service.aggregation.feature.bucket.strategy;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.aggregation.feature.bucket.strategy.AbstractVpnSessionFeatureBucketStrategyFactory;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyStore;
import fortscale.aggregation.feature.bucket.strategy.VpnSessionFeatureBucketStrategy;

public class VpnSessionFeatureBucketStrategyFactorySamza extends AbstractVpnSessionFeatureBucketStrategyFactory
		implements FeatureBucketStrategyFactorySamza  {
	FeatureBucketStrategyStore featureBucketStrategyStore = null;

	@Override
	protected VpnSessionFeatureBucketStrategy createVpnSessionFeatureBucketStrategy(String strategyName, long maxSessionDuration){
		VpnSessionFeatureBucketStrategy ret = new VpnSessionFeatureBucketStrategy(strategyName, maxSessionDuration);
		if(featureBucketStrategyStore != null){
			ret.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		}
		return ret;
	}

	@Override
	public void init(ExtendedSamzaTaskContext context) {
		featureBucketStrategyStore = new FeatureBucketStrategyLevelDbStore(context);
		for (VpnSessionFeatureBucketStrategy featureBucketStrategy : featureBucketStrategies) {
			featureBucketStrategy.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		}
	}
}
