package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction = true)
public class FeatureBucketStrategiesFactorySamza extends FeatureBucketStrategiesFactory {
	@Autowired
	private FeatureBucketStrategyStore featureBucketStrategyStore;

	public FeatureBucketStrategiesFactorySamza(ExtendedSamzaTaskContext context) {
		// FeatureBucketStrategyStore bucketStrategyStore = new FeatureBucketStrategyLevelDbStore(context);
		for (FeatureBucketStrategyFactory featureBucketStrategyFactory : featureBucketStrategyFactoryMap.values()) {
			if (featureBucketStrategyFactory instanceof FeatureBucketStrategyFactorySamza) {
				((FeatureBucketStrategyFactorySamza)featureBucketStrategyFactory).setStrategyStore(featureBucketStrategyStore);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		registerFeatureBucketStrategyFactory(FixedDurationFeatureBucketStrategyFactory.STRATEGY_TYPE, new FixedDurationFeatureBucketStrategyFactory());
		registerFeatureBucketStrategyFactory(InactivityFeatureBucketStrategyFactory.STRATEGY_TYPE, new InactivityFeatureBucketStrategyFactory());
		super.afterPropertiesSet();
	}
}
