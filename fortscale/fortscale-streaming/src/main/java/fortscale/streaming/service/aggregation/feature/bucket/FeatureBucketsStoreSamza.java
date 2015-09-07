package fortscale.streaming.service.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.streaming.ExtendedSamzaTaskContext;

@Configurable(preConstruction=true)
public class FeatureBucketsStoreSamza extends FeatureBucketsMongoStore {
	
	private static final String STORE_NAME = "feature_buckets_store";

	private KeyValueStore<String, FeatureBucket> featureBucketStore;
	
	@Autowired
	private DataSourcesSyncTimer dataSourcesSyncTimer;
	
	
	@SuppressWarnings("unchecked")
	public FeatureBucketsStoreSamza(ExtendedSamzaTaskContext context) {
		featureBucketStore = (KeyValueStore<String, FeatureBucket>)context.getStore(STORE_NAME);
	}
	
	
	public void sync(FeatureBucketConf featureBucketConf, String bucketId){
		FeatureBucket featureBucket = featureBucketStore.get(bucketId);
		if(featureBucket != null){
			storeFeatureBucket(featureBucketConf, featureBucket);
		}
	}

	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {
		List<FeatureBucket> superRet = super.updateFeatureBucketsEndTime(featureBucketConf, strategyId, newCloseTime);
		if(superRet.isEmpty()){
			return superRet;
		}
		
		List<FeatureBucket> ret = new ArrayList<>();
		for(FeatureBucket featureBucket: superRet){
			FeatureBucket featureBucketSamza = featureBucketStore.get(featureBucket.getBucketId());
			featureBucketSamza.setEndTime(newCloseTime);
			featureBucketStore.put(featureBucketSamza.getBucketId(), featureBucketSamza);
			ret.add(featureBucketSamza);
			FeatureBucketsTimerListener featureBucketsTimerListener = new FeatureBucketsTimerListener(featureBucketConf, featureBucketSamza.getBucketId());
			dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(featureBucketConf.getDataSources(), featureBucket.getEndTime()+1, featureBucketsTimerListener);
		}
		return ret;
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId) {
		return featureBucketStore.get(bucketId);
	}

	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) {
		String bucketId = featureBucket.getId();
		if(bucketId == null || featureBucket.getEndTime() <= dataSourcesSyncTimer.getLastEventEpochtime()){
			if(bucketId == null){
				FeatureBucketsTimerListener featureBucketsTimerListener = new FeatureBucketsTimerListener(featureBucketConf, bucketId);
				dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(featureBucketConf.getDataSources(), featureBucket.getEndTime()+1, featureBucketsTimerListener);
			}
			super.storeFeatureBucket(featureBucketConf, featureBucket);
		}

		featureBucketStore.put(bucketId, featureBucket);
	}
}
