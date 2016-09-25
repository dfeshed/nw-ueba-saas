package fortscale.aggregation.feature.bucket.strategy;

import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class FeatureBucketStrategyInMemoryStore implements FeatureBucketStrategyStore {
	@Autowired
	private StatsService statsService;
	
	private Map<String, List<FeatureBucketStrategyData>> startegyEventContextIdToData = new HashMap<String, List<FeatureBucketStrategyData>>();
	private FeatureBucketStrategyStoreMetrics metrics;

	public FeatureBucketStrategyInMemoryStore() {
		metrics = new FeatureBucketStrategyStoreMetrics(statsService, "inMemory");
	}

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime) {
		List<FeatureBucketStrategyData> strategyDataList = startegyEventContextIdToData.get(strategyEventContextId);
		if(strategyDataList == null){
			return null;
		}
		// Assume that the list is in ascending order over the start time
		for (int i = strategyDataList.size() - 1; i >= 0; i--) {
			FeatureBucketStrategyData featureBucketStrategyData = strategyDataList.get(i);
			if (featureBucketStrategyData.getStartTime() <= latestStartTime) {
				return featureBucketStrategyData;
			}
		}
		return null;
	}

	@Override
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData) {
		String strategyContextId = featureBucketStrategyData.getStrategyEventContextId();
		List<FeatureBucketStrategyData> strategyDataList = startegyEventContextIdToData.get(strategyContextId);

		if (strategyDataList == null) {
			strategyDataList = new ArrayList<>();
		}

		int i;
		for (i = strategyDataList.size() - 1; i >= 0; i--) {
			FeatureBucketStrategyData iter = strategyDataList.get(i);
			if (featureBucketStrategyData.getStartTime() > iter.getStartTime()) {
				strategyDataList.add(i + 1, featureBucketStrategyData);
				break;
			} else if (featureBucketStrategyData.getStartTime() == iter.getStartTime() && 
					featureBucketStrategyData.getContextMap().equals(iter.getContextMap())) {
				strategyDataList.set(i, featureBucketStrategyData);
				break;
			}
		}

		if (i == -1) {
			strategyDataList.add(0, featureBucketStrategyData);
		}

		// Write back to store the updated list
		startegyEventContextIdToData.put(strategyContextId, strategyDataList);
		metrics.saves++;
	}

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime,	Map<String, String> contextMap) {
		List<FeatureBucketStrategyData> strategyDataList = startegyEventContextIdToData.get(strategyEventContextId);
		if(strategyDataList == null){
			return null;
		}
		// Assume that the list is in ascending order over the start time
		for (int i = strategyDataList.size() - 1; i >= 0; i--) {
			FeatureBucketStrategyData featureBucketStrategyData = strategyDataList.get(i);
			if (featureBucketStrategyData.getStartTime() <= latestStartTime && featureBucketStrategyData.getContextMap().equals(contextMap)) {
				return featureBucketStrategyData;
			}
		}
		return null;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyDataContainsEventTime(String strategyEventContextId,	long eventTime) {
		List<FeatureBucketStrategyData> strategyDataList = startegyEventContextIdToData.get(strategyEventContextId);
		if(strategyDataList == null){
			return null;
		}
		
		List<FeatureBucketStrategyData> ret = new ArrayList<>();
		for (FeatureBucketStrategyData featureBucketStrategyData: strategyDataList) {
			if (featureBucketStrategyData.getStartTime() <= eventTime && featureBucketStrategyData.getEndTime() > eventTime) {
				ret.add(featureBucketStrategyData);
			}
		}
		return ret;
	}

}
