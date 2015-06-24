package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;

public abstract class FeatureBucketsService {
		
	public List<FeatureBucket> updateFeatureBucketsWithNewBucketEndTime(List<FeatureBucketConf> featureBucketConfs, List<FeatureBucketStrategyData> updatedFeatureBucketStrategyData){
		Map<String, FeatureBucketStrategyData> strategyNameToDataMap = new HashMap<String, FeatureBucketStrategyData>();
		for(FeatureBucketStrategyData featureBucketStrategyData: updatedFeatureBucketStrategyData){
			strategyNameToDataMap.put(featureBucketStrategyData.getStrategyName(), featureBucketStrategyData);
		}
		List<FeatureBucket> ret = new ArrayList<>();
		for(FeatureBucketConf featureBucketConf: featureBucketConfs){
			FeatureBucketStrategyData featureBucketStrategyData = strategyNameToDataMap.get(featureBucketConf.getStrategyName());
			if(featureBucketStrategyData != null){
				List<FeatureBucket> updatedBuckets = getFeatureBucketsStore().updateFeatureBucketsEndTime(featureBucketConf, featureBucketStrategyData.getStrategyId(), featureBucketStrategyData.getEndTime());
				if(updatedBuckets != null){
					ret.addAll(updatedBuckets);
				}
			}
		}
		
		return ret;
	}

	public List<FeatureBucket> updateFeatureBucketsWithNewEvent(JSONObject event, List<FeatureBucketConf> featureBucketConfs) {
		List<FeatureBucket> newFeatureBuckets = new ArrayList<FeatureBucket>();
		for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
			List<FeatureBucketStrategyData> featureBucketStrategyDatas = getFeatureBucketStrategyService().getFeatureBucketStrategyData(event, featureBucketConf);
			for(FeatureBucketStrategyData strategyData: featureBucketStrategyDatas){
				FeatureBucket featureBucket = getFeatureBucketsStore().getFeatureBucket(featureBucketConf, strategyData.getStrategyId());
				if(featureBucket == null){
					featureBucket = createNewFeatureBucket(featureBucketConf, strategyData);
					newFeatureBuckets.add(featureBucket);
				}
				updateFeatureBucket(featureBucket, featureBucketConf);
				storeFeatureBucket(featureBucket, featureBucketConf);
			}	
		}
		
		return newFeatureBuckets;
	}
	
	private void updateFeatureBucket(FeatureBucket featureBucket, FeatureBucketConf featureBucketConf){
		//TODO
	}
	
	private void storeFeatureBucket(FeatureBucket featureBucket, FeatureBucketConf featureBucketConf){
		//TODO
	}
	
	private FeatureBucket createNewFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucketStrategyData strategyData){
		//TODO
		return null;
	}

	protected abstract FeatureBucketsStore getFeatureBucketsStore();

	protected abstract FeatureBucketStrategyService getFeatureBucketStrategyService();
	
	
}
