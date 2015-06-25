package fortscale.streaming.service.aggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;
import fortscale.utils.logging.Logger;

public abstract class FeatureBucketsService {
	private static final Logger logger = Logger.getLogger(FeatureBucketsService.class);
	
	private static final String BUCKET_ID_BUILDER_SEPERATOR = "_";
		
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
			try{
				for(FeatureBucketStrategyData strategyData: featureBucketStrategyDatas){
					FeatureBucket featureBucket = getFeatureBucketsStore().getFeatureBucket(featureBucketConf, getBucketId(event, featureBucketConf, strategyData.getStrategyId()));
					if(featureBucket == null){
						featureBucket = createNewFeatureBucket(event, featureBucketConf, strategyData);
						newFeatureBuckets.add(featureBucket);
					}
					updateFeatureBucket(featureBucket, featureBucketConf);
					storeFeatureBucket(featureBucket, featureBucketConf);
				}	
			} catch(Exception e){
				logger.error("got an exception while updating buckets with new event", e);
			}
		}
		
		return newFeatureBuckets;
	}
	
	private String getBucketId(JSONObject event, FeatureBucketConf featureBucketConf, String strategyId){
		List<String> sorted = new ArrayList<>(featureBucketConf.getContextFieldNames());
		Collections.sort(sorted);
		StringBuilder builder = new StringBuilder();
		builder.append(strategyId).append(BUCKET_ID_BUILDER_SEPERATOR);
		for(String contextFieldName: featureBucketConf.getContextFieldNames()){
			String contextValue = (String) event.get(contextFieldName);
			if(contextValue == null){
				throw new IllegalArgumentException(String.format("the parameter %s is not contained in the json object %s", contextFieldName,event.toJSONString()));
			}
			builder.append(contextFieldName).append(BUCKET_ID_BUILDER_SEPERATOR).append(contextValue).append(BUCKET_ID_BUILDER_SEPERATOR);
		}
		return builder.toString();
	}
	
	private void updateFeatureBucket(FeatureBucket featureBucket, FeatureBucketConf featureBucketConf){
		//TODO
	}
	
	private void storeFeatureBucket(FeatureBucket featureBucket, FeatureBucketConf featureBucketConf){
		getFeatureBucketsStore().storeFeatureBucket(featureBucketConf, featureBucket);
	}
	
	private FeatureBucket createNewFeatureBucket(JSONObject event, FeatureBucketConf featureBucketConf, FeatureBucketStrategyData strategyData){
		FeatureBucket ret = new FeatureBucket();
		ret.setFeatureBucketConfName(featureBucketConf.getName());
		ret.setBucketId(getBucketId(event, featureBucketConf, strategyData.getStrategyId()));
		ret.setStrategyId(strategyData.getStrategyId());
		ret.setContextFieldNames(featureBucketConf.getContextFieldNames());
		ret.setDataSources(featureBucketConf.getDataSources());
		ret.setStartTime(strategyData.getStartTime());
		ret.setEndTime(strategyData.getEndTime());
		for(String contextFieldName: featureBucketConf.getContextFieldNames()){
			String contextValue = (String) event.get(contextFieldName);
			ret.addToContextFieldNameToValueMap(contextFieldName, contextValue);
		}
		return ret;
	}

	protected abstract FeatureBucketsStore getFeatureBucketsStore();

	protected abstract FeatureBucketStrategyService getFeatureBucketStrategyService();
	
	
}
