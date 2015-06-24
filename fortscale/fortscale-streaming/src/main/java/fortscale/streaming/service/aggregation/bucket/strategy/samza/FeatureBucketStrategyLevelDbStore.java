package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import org.apache.samza.storage.kv.KeyValueStore;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;





public class FeatureBucketStrategyLevelDbStore implements FeatureBucketStrategyStore {
	
	private static final String	STORE_NAME = 	"strategy_store";
	
	private KeyValueStore<String, FeatureBucketStrategyData> strategyStore;
	
	@SuppressWarnings("unchecked")
	public FeatureBucketStrategyLevelDbStore(ExtendedSamzaTaskContext context){
		strategyStore = (KeyValueStore<String, FeatureBucketStrategyData>) context.getStore(STORE_NAME);
	}

	@Override
	public void storeStrategyData(FeatureBucketStrategyData featureBucketStrategyData) {
		strategyStore.put(featureBucketStrategyData.getStrategyId(), featureBucketStrategyData);
	}

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
