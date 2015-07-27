package fortscale.aggregation.feature.bucket.strategy;

import org.springframework.beans.factory.annotation.Autowired;

public class VpnSessionFeatureBucketStrategyFactory extends AbstractVpnSessionFeatureBucketStrategyFactory {
	@Autowired
	private FeatureBucketStrategyStore featureBucketStrategyStore;

	protected VpnSessionFeatureBucketStrategy createVpnSessionFeatureBucketStrategy(String strategyName, long maxSessionDuration){
		VpnSessionFeatureBucketStrategy ret = new VpnSessionFeatureBucketStrategy(strategyName, maxSessionDuration);
		ret.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		return ret;
	}
}
