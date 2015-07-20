package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.AbstractVpnSessionFeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;
import fortscale.streaming.service.aggregation.bucket.strategy.VpnSessionFeatureBucketStrategy;

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
