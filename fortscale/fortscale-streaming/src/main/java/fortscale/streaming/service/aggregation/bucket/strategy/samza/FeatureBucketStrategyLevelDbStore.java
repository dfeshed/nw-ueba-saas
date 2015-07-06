package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;
import org.apache.samza.storage.kv.KeyValueStore;

import java.util.ArrayList;
import java.util.List;

public class FeatureBucketStrategyLevelDbStore implements FeatureBucketStrategyStore {
	private static final String STORE_NAME = "strategy_store";

	private KeyValueStore<String, List<FeatureBucketStrategyData>> strategyStore;

	@SuppressWarnings("unchecked")
	public FeatureBucketStrategyLevelDbStore(ExtendedSamzaTaskContext context) {
		strategyStore = (KeyValueStore<String, List<FeatureBucketStrategyData>>)context.getStore(STORE_NAME);
	}

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime) {
		List<FeatureBucketStrategyData> strategyDataList = strategyStore.get(strategyContextId);
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
		List<FeatureBucketStrategyData> strategyDataList = strategyStore.get(strategyContextId);

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
		strategyStore.put(strategyContextId, strategyDataList);
	}
}
