package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategiesFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;
import fortscale.streaming.service.aggregation.bucket.strategy.FixedDurationFeatureBucketStrategyFactory;


@Configurable(preConstruction=true)
public class FeatureBucketStrategiesFactorySamza extends FeatureBucketStrategiesFactory {
	
	@Autowired
	private FeatureBucketStrategyStore featureBucketStrategyStore;
	
	public FeatureBucketStrategiesFactorySamza(ExtendedSamzaTaskContext context){
//		FeatureBucketStrategyStore bucketStrategyStore = new FeatureBucketStrategyLevelDbStore(context);
		for(FeatureBucketStrategyFactory featureBucketStrategyFactory: featureBucketStrategyFactoryMap.values()){
			if(featureBucketStrategyFactory instanceof FeatureBucketStrategyFactorySamza){
				((FeatureBucketStrategyFactorySamza)featureBucketStrategyFactory).setStrategyStore(featureBucketStrategyStore);
			}
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		registerFeatureBucketStrategyFactory(FixedDurationFeatureBucketStrategyFactory.STRATEGY_TYPE, new FixedDurationFeatureBucketStrategyFactory());
		super.afterPropertiesSet();
	}
}
