package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import org.springframework.beans.factory.annotation.Configurable;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.AbstractUserInactivityFeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategiesFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FixedDurationFeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.UserInactivityFeatureBucketStrategyFactory;

@Configurable(preConstruction = true)
public class FeatureBucketStrategiesFactorySamza extends FeatureBucketStrategiesFactory {
	

	public FeatureBucketStrategiesFactorySamza(ExtendedSamzaTaskContext context) {
		// FeatureBucketStrategyStore bucketStrategyStore = new FeatureBucketStrategyLevelDbStore(context);
		for (FeatureBucketStrategyFactory featureBucketStrategyFactory : featureBucketStrategyFactoryMap.values()) {
			if (featureBucketStrategyFactory instanceof FeatureBucketStrategyFactorySamza) {
				((FeatureBucketStrategyFactorySamza)featureBucketStrategyFactory).init(context);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		registerFeatureBucketStrategyFactory(FixedDurationFeatureBucketStrategyFactory.STRATEGY_TYPE, new FixedDurationFeatureBucketStrategyFactory());
		registerFeatureBucketStrategyFactory(AbstractUserInactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, new UserInactivityFeatureBucketStrategyFactory());
		super.afterPropertiesSet();
	}
}
