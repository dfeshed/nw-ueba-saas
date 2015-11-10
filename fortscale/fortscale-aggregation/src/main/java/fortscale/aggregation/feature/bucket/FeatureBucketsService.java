package fortscale.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.aggregation.feature.extraction.Event;
import fortscale.aggregation.feature.extraction.FeatureExtractService;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.utils.logging.Logger;

public abstract class FeatureBucketsService {
	private static final Logger logger = Logger.getLogger(FeatureBucketsService.class);
	private static final String BUCKET_ID_BUILDER_SEPARATOR = "###";
	private static final String CONTEXT_ID_SEPARATOR = "###";

	public List<FeatureBucket> updateFeatureBucketsWithNewBucketEndTime(List<FeatureBucketConf> featureBucketConfs, List<FeatureBucketStrategyData> updatedFeatureBucketStrategyData){
		if(updatedFeatureBucketStrategyData == null || updatedFeatureBucketStrategyData.isEmpty()){
			return Collections.emptyList();
		}
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

	public List<FeatureBucket> updateFeatureBucketsWithNewEvent(Event event, List<FeatureBucketConf> featureBucketConfs) {
		List<FeatureBucket> newFeatureBuckets = new ArrayList<>();
		for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
			List<FeatureBucketStrategyData> featureBucketStrategyDataList = getFeatureBucketStrategyService().getFeatureBucketStrategyData(event, featureBucketConf);
			try {
				for (FeatureBucketStrategyData strategyData : featureBucketStrategyDataList) {
					String bucketId = getBucketId(event, featureBucketConf, strategyData.getStrategyId());
					if (bucketId == null) {
						break;
					}
					FeatureBucket featureBucket = getFeatureBucketsStore().getFeatureBucket(featureBucketConf, bucketId);
					if (featureBucket == null) {
						featureBucket = createNewFeatureBucket(event, featureBucketConf, strategyData);
						if (featureBucket == null) {
							continue;
						} else {
							newFeatureBuckets.add(featureBucket);
						}
					}
					updateFeatureBucket(event, featureBucket, featureBucketConf);
					storeFeatureBucket(featureBucket, featureBucketConf);
				}
			} catch (Exception e) {
				logger.error("Got an exception while updating buckets with new event", e);
			}
		}
		return newFeatureBuckets;
	}

	private String getBucketId(Event event, FeatureBucketConf featureBucketConf, String strategyId) {
		List<String> sorted = new ArrayList<>(featureBucketConf.getContextFieldNames());
		Collections.sort(sorted);
		StringBuilder builder = new StringBuilder();
		builder.append(strategyId).append(BUCKET_ID_BUILDER_SEPARATOR);

		for (int i = 0; i < sorted.size(); i++) {
			String contextFieldName = sorted.get(i);
			String contextValue = (String)event.get(contextFieldName);
			// Return null as the bucket ID if one of the contexts is missing
			if (StringUtils.isBlank(contextValue)) {
				return null;
			}
			builder.append(contextFieldName).append(BUCKET_ID_BUILDER_SEPARATOR).append(contextValue);
			if (i != sorted.size() - 1) {
				builder.append(BUCKET_ID_BUILDER_SEPARATOR);
			}
		}

		return builder.toString();
	}

	private void updateFeatureBucket(Event event, FeatureBucket featureBucket, FeatureBucketConf featureBucketConf){
		Map<String, Feature> featuresMap = getFeatureExtractService().extract(featureBucketConf.getAllFeatureNames(), event);
		Map<String, Feature> aggrFeaturesMap = getAggrFeatureFunctionsService().updateAggrFeatures(event, featureBucketConf.getAggrFeatureConfs(), featureBucket.getAggregatedFeatures(), featuresMap);
		featureBucket.setAggregatedFeatures(aggrFeaturesMap);
	}
	
	
	private void storeFeatureBucket(FeatureBucket featureBucket, FeatureBucketConf featureBucketConf) throws Exception{
		if(featureBucket.getContextId() == null){
			String contextId = buildContextId(featureBucket.getContextFieldNameToValueMap());
			featureBucket.setContextId(contextId);
		}
		getFeatureBucketsStore().storeFeatureBucket(featureBucketConf, featureBucket);
	}
	
	private String buildContextId(Map<String, String> context) {
		List<Map.Entry<String, String>> listOfEntries = new ArrayList<>(context.entrySet());
		Collections.sort(listOfEntries, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> entry1, Map.Entry<String, String> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});

		List<String> listOfPairs = new ArrayList<>();
		for (Map.Entry<String, String> entry : listOfEntries) {
			listOfPairs.add(String.format("%s%s%s", entry.getKey(), CONTEXT_ID_SEPARATOR, entry.getValue()));
		}

		return StringUtils.join(listOfPairs, CONTEXT_ID_SEPARATOR);
	}

	private FeatureBucket createNewFeatureBucket(Event event, FeatureBucketConf featureBucketConf, FeatureBucketStrategyData strategyData) {
		String bucketId = getBucketId(event, featureBucketConf, strategyData.getStrategyId());
		if (bucketId == null) {
			return null;
		}

		FeatureBucket ret = new FeatureBucket();
		ret.setFeatureBucketConfName(featureBucketConf.getName());
		ret.setBucketId(bucketId);
		ret.setStrategyId(strategyData.getStrategyId());
		ret.setContextFieldNames(featureBucketConf.getContextFieldNames());
		ret.setDataSources(featureBucketConf.getDataSources());
		ret.setStartTime(strategyData.getStartTime());
		ret.setEndTime(strategyData.getEndTime());
		ret.setCreatedAt(new Date());

		for (String contextFieldName : featureBucketConf.getContextFieldNames()) {
			String contextValue = (String)event.get(contextFieldName);
			ret.addToContextFieldNameToValueMap(contextFieldName, contextValue);
		}

		return ret;
	}

	protected abstract FeatureBucketsStore getFeatureBucketsStore();

	protected abstract FeatureBucketStrategyService getFeatureBucketStrategyService();
	
	protected abstract FeatureExtractService getFeatureExtractService();
	
	protected abstract IAggrFeatureFunctionsService getAggrFeatureFunctionsService();

	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId) {
		return getFeatureBucketsStore().getFeatureBucket(featureBucketConf, bucketId);
	}
}
