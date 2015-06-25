package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import java.util.List;

import org.apache.samza.storage.kv.KeyValueStore;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyStore;





public class FeatureBucketStrategyLevelDbStore implements FeatureBucketStrategyStore {
	
	private static final String	STORE_NAME = 	"strategy_store";
	
	private KeyValueStore<String, List<FeatureBucketStrategyData>> strategyStore;
	
	@SuppressWarnings("unchecked")
	public FeatureBucketStrategyLevelDbStore(ExtendedSamzaTaskContext context){
		strategyStore = (KeyValueStore<String, List<FeatureBucketStrategyData>>) context.getStore(STORE_NAME);
	}

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime) {
		List<FeatureBucketStrategyData> bucketStrategyDatas = strategyStore.get(strategyContextId);
		//assume that the list is in ascending order over the start time.
		for(int i = bucketStrategyDatas.size()-1; i>=0; i--){
			FeatureBucketStrategyData featureBucketStrategyData = bucketStrategyDatas.get(i);
			if(featureBucketStrategyData.getStartTime()<latestStartTime){
				return featureBucketStrategyData;
			}
		}
		return null;
	}

}
