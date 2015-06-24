package fortscale.streaming.service.aggregation.bucket.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minidev.json.JSONObject;
import fortscale.streaming.service.aggregation.FeatureBucketConf;
import fortscale.streaming.service.aggregation.FeatureBucketsStore;


public abstract class FeatureBucketStrategyService {
	
	

	public List<FeatureBucketStrategyData> updateStrategies(JSONObject message) {
		List<FeatureBucketStrategyData> ret = new ArrayList<>();
		for(FeatureBucketStrategy strategy: getAllStrategies()){
			FeatureBucketStrategyData updatedStrategyData = strategy.update(message);
			if(updatedStrategyData != null){
				ret.add(updatedStrategyData);
			}
		}

		return ret;
	}
	
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(JSONObject event, FeatureBucketConf featureBucketConf){
		FeatureBucketStrategy strategy = getFeatureBucketStrategy(featureBucketConf.getStrategyName());
		return strategy.getFeatureBucketStrategyData(event, featureBucketConf);
	}

	private FeatureBucketStrategy getFeatureBucketStrategy(String strategyName) {
		return getFeatureBucketStrategiesFactory().getFeatureBucketStrategy(strategyName);
	}
	
	private Collection<FeatureBucketStrategy> getAllStrategies(){
		return getFeatureBucketStrategiesFactory().getAllStrategies();
	}

	public abstract FeatureBucketStrategiesFactory getFeatureBucketStrategiesFactory();

	public abstract FeatureBucketsStore getFeatureBucketsStore();
	
	
}
