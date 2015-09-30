package fortscale.streaming.service.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.streaming.ExtendedSamzaTaskContext;

import static fortscale.streaming.ConfigUtils.getConfigString;

@Configurable(preConstruction=true)
public class FeatureBucketsStoreSamza extends FeatureBucketsMongoStore {
	
	private static final String STORE_NAME_PROPERTY = "fortscale.feature.buckets.store.name";

	private KeyValueStore<String, FeatureBucket> featureBucketStore;
	
	@Autowired
	private DataSourcesSyncTimer dataSourcesSyncTimer;
	
	
	@SuppressWarnings("unchecked")
	public FeatureBucketsStoreSamza(ExtendedSamzaTaskContext context) {
		Assert.notNull(context);
		Config config = context.getConfig();
		String storeName = getConfigString(config, STORE_NAME_PROPERTY);
		featureBucketStore = (KeyValueStore<String, FeatureBucket>)context.getStore(storeName);
		Assert.notNull(featureBucketStore);
	}
	
	
	public void sync(FeatureBucketConf featureBucketConf, String bucketId) throws Exception{
		FeatureBucket featureBucket = featureBucketStore.get(getBucketKey(featureBucketConf.getName(), bucketId));
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
			FeatureBucket featureBucketSamza = featureBucketStore.get(getBucketKey(featureBucket));
			featureBucketSamza.setEndTime(newCloseTime);
			featureBucketStore.put(getBucketKey(featureBucket), featureBucketSamza);
			ret.add(featureBucketSamza);
			FeatureBucketsTimerListener featureBucketsTimerListener = new FeatureBucketsTimerListener(this, featureBucketConf, featureBucketSamza.getBucketId());
			dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(featureBucketConf.getDataSources(), featureBucket.getEndTime()+1, featureBucketsTimerListener);
		}
		return ret;
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId) {
		return featureBucketStore.get(getBucketKey(featureBucketConf.getName(), bucketId));
	}

	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		String id = featureBucket.getId();
		String bucketId = featureBucket.getBucketId();

		// Storing the bucket at the first time when it is created (id==null) and after endTime is passed
		if(id == null || featureBucket.getEndTime() < dataSourcesSyncTimer.getLastEventEpochtime()){
			if(id == null){
				FeatureBucketsTimerListener featureBucketsTimerListener = new FeatureBucketsTimerListener(this, featureBucketConf, bucketId);
				dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(featureBucketConf.getDataSources(), featureBucket.getEndTime()+1, featureBucketsTimerListener);
			}
			super.storeFeatureBucket(featureBucketConf, featureBucket);

		}

		if(id==null) {
			// At the first time the bucket is stored in mongo it gets an id, so we
			// need to get the updated bucket with the id otherwise when coming to save
			// the bucket in mongo next time it will throw com.mongodb.MongoException$DuplicateKey exception
			featureBucket = super.getFeatureBucket(featureBucketConf, bucketId);
		}

		String key = getBucketKey(featureBucket);
		featureBucketStore.put(key, featureBucket);
	}

	private String getBucketKey(FeatureBucket featureBucket) {
		return getBucketKey(featureBucket.getFeatureBucketConfName(), featureBucket.getBucketId());
	}

	private String getBucketKey(String featureBucketConfName, String bucketId) {
		return featureBucketConfName + "." + bucketId;
	}
}
