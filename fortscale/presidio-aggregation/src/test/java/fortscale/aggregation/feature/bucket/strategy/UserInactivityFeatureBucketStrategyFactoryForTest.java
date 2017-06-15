package fortscale.aggregation.feature.bucket.strategy;

import java.util.List;

public class UserInactivityFeatureBucketStrategyFactoryForTest extends AbstractUserInactivityFeatureBucketStrategyFactory{
	
	FeatureBucketStrategyStore featureBucketStrategyStore;
	
	public UserInactivityFeatureBucketStrategyFactoryForTest(FeatureBucketStrategyStore store){
		featureBucketStrategyStore = store;
	}
	
	@Override
	protected UserInactivityFeatureBucketStrategy createUserInactivityFeatureBucketStrategy(String strategyName, List<String> dataSources, long inactivityDurationInMinutes, long endTimeDeltaInMinutes){
		UserInactivityFeatureBucketStrategy ret = new UserInactivityFeatureBucketStrategy(strategyName, dataSources, inactivityDurationInMinutes, endTimeDeltaInMinutes);
		ret.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		return ret;
	}
}
