package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import java.util.List;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.AbstractUserInactivityFeatureBucketStrategyFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;
import fortscale.streaming.service.aggregation.bucket.strategy.UserInactivityFeatureBucketStrategy;

public class UserInactivityFeatureBucketStrategyFactorySamza extends AbstractUserInactivityFeatureBucketStrategyFactory implements FeatureBucketStrategyFactorySamza {
	
	FeatureBucketStrategyStore featureBucketStrategyStore = null;
	
	@Override
	protected UserInactivityFeatureBucketStrategy createUserInactivityFeatureBucketStrategy(String strategyName, List<String> dataSources, long inactivityDurationInMinutes, long endTimeDeltaInMinutes){
		UserInactivityFeatureBucketStrategy ret = new UserInactivityFeatureBucketStrategy(strategyName, dataSources, inactivityDurationInMinutes, endTimeDeltaInMinutes);
		if(featureBucketStrategyStore != null){
			ret.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		}
		return ret;
	}
	
	@Override
	public void init(ExtendedSamzaTaskContext context) {
		featureBucketStrategyStore = new FeatureBucketStrategyLevelDbStore(context);
		for (UserInactivityFeatureBucketStrategy featureBucketStrategy : featureBucketStrategies) {
			featureBucketStrategy.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		}
	}
}
