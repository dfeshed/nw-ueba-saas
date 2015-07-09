package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import fortscale.streaming.service.aggregation.bucket.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import fortscale.streaming.ExtendedSamzaTaskContext;

@Configurable(preConstruction = true) public class FeatureBucketStrategiesFactorySamza
		extends FeatureBucketStrategiesFactory {

	@Autowired private UserInactivityFeatureBucketStrategyFactory userInactivityFeatureBucketStrategyFactory;
	@Autowired private VpnSessionFeatureBucketStrategyFactory vpnSessionFeatureBucketStrategyFactory;

	public FeatureBucketStrategiesFactorySamza(ExtendedSamzaTaskContext context) {
		// FeatureBucketStrategyStore bucketStrategyStore = new FeatureBucketStrategyLevelDbStore(context);
		for (FeatureBucketStrategyFactory featureBucketStrategyFactory : featureBucketStrategyFactoryMap.values()) {
			if (featureBucketStrategyFactory instanceof FeatureBucketStrategyFactorySamza) {
				((FeatureBucketStrategyFactorySamza) featureBucketStrategyFactory).init(context);
			}
		}
	}

	@Override public void afterPropertiesSet() throws Exception {
		registerFeatureBucketStrategyFactory(FixedDurationFeatureBucketStrategyFactory.STRATEGY_TYPE, new FixedDurationFeatureBucketStrategyFactory());
		registerFeatureBucketStrategyFactory(AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, userInactivityFeatureBucketStrategyFactory);
		registerFeatureBucketStrategyFactory(AbstractVpnSessionFeatureBucketStrategyFactory.STRATEGY_TYPE, vpnSessionFeatureBucketStrategyFactory);
		super.afterPropertiesSet();
	}
}
