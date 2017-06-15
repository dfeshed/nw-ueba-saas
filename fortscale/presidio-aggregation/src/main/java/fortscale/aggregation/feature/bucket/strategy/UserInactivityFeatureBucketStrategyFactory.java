package fortscale.aggregation.feature.bucket.strategy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


public class UserInactivityFeatureBucketStrategyFactory extends	AbstractUserInactivityFeatureBucketStrategyFactory {
	@Autowired
	private FeatureBucketStrategyStore featureBucketStrategyStore;
	
	protected UserInactivityFeatureBucketStrategy createUserInactivityFeatureBucketStrategy(String strategyName, List<String> dataSources, long inactivityDurationInMinutes, long endTimeDeltaInMinutes){
		UserInactivityFeatureBucketStrategy ret = new UserInactivityFeatureBucketStrategy(strategyName, dataSources, inactivityDurationInMinutes, endTimeDeltaInMinutes);
		ret.setFeatureBucketStrategyStore(featureBucketStrategyStore);
		return ret;
	}
}
