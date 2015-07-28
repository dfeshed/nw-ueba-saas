package fortscale.aggregation.feature.bucket.strategy;


public class VpnSessionFeatureBucketStrategyFactoryForTest extends AbstractVpnSessionFeatureBucketStrategyFactory {

	FeatureBucketStrategyStore featureBucketStrategyStore;
	
	public VpnSessionFeatureBucketStrategyFactoryForTest(FeatureBucketStrategyStore store){
		featureBucketStrategyStore = store;
	}
	
	protected VpnSessionFeatureBucketStrategy createVpnSessionFeatureBucketStrategy(String strategyName, long maxSessionDuration){
		VpnSessionFeatureBucketStrategy ret = new VpnSessionFeatureBucketStrategy(strategyName, maxSessionDuration);
		ret.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		return ret;
	}
}
