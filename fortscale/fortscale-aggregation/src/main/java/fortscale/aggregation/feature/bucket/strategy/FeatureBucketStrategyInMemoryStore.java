package fortscale.aggregation.feature.bucket.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureBucketStrategyInMemoryStore implements FeatureBucketStrategyStore {
	
	private Map<String, List<FeatureBucketStrategyData>> startegyContextIdToData = new HashMap<String, List<FeatureBucketStrategyData>>();

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime) {
		List<FeatureBucketStrategyData> strategyDataList = startegyContextIdToData.get(strategyContextId);
		if(strategyDataList == null){
			return null;
		}
		// Assume that the list is in ascending order over the start time
		for (int i = strategyDataList.size() - 1; i >= 0; i--) {
			FeatureBucketStrategyData featureBucketStrategyData = strategyDataList.get(i);
			if (featureBucketStrategyData.getStartTime() < latestStartTime) {
				return featureBucketStrategyData;
			}
		}
		return null;
	}

	@Override
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData) {
		String strategyContextId = featureBucketStrategyData.getStrategyContextId();
		List<FeatureBucketStrategyData> strategyDataList = startegyContextIdToData.get(strategyContextId);

		if (strategyDataList == null) {
			strategyDataList = new ArrayList<>();
		}

		int i;
		for (i = strategyDataList.size() - 1; i >= 0; i--) {
			if (featureBucketStrategyData.getStartTime() > strategyDataList.get(i).getStartTime()) {
				strategyDataList.add(i + 1, featureBucketStrategyData);
				break;
			} else if (featureBucketStrategyData.getStartTime() == strategyDataList.get(i).getStartTime()) {
				strategyDataList.set(i, featureBucketStrategyData);
				break;
			}
		}

		if (i == -1) {
			strategyDataList.add(0, featureBucketStrategyData);
		}

		// Write back to store the updated list
		startegyContextIdToData.put(strategyContextId, strategyDataList);
	}

}
