package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import org.springframework.beans.factory.annotation.Configurable;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategiesFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FixedDurationFeatureBucketStrategyFactory;


@Configurable(preConstruction=true)
public class FeatureBucketStrategiesFactorySamza extends FeatureBucketStrategiesFactory {
	
	public FeatureBucketStrategiesFactorySamza(ExtendedSamzaTaskContext context){
		FeatureBucketStrategyLevelDbStore bucketStrategyLevelDbStore = new FeatureBucketStrategyLevelDbStore(context);
		for(FeatureBucketStrategyFactory featureBucketStrategyFactory: featureBucketStrategyFactoryMap.values()){
			if(featureBucketStrategyFactory instanceof FeatureBucketStrategyFactorySamza){
				((FeatureBucketStrategyFactorySamza)featureBucketStrategyFactory).setStrategyStore(bucketStrategyLevelDbStore);
			}
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		registerFeatureBucketStrategyFactory(FixedDurationFeatureBucketStrategyFactory.STRATEGY_TYPE, new FixedDurationFeatureBucketStrategyFactory());
		super.afterPropertiesSet();
	}
}
