package fortscale.streaming.service.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.feature.bucket.repository.FeatureBucketMetadata;
import fortscale.streaming.service.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import fortscale.utils.logging.Logger;

import static fortscale.streaming.ConfigUtils.getConfigString;

@Configurable(preConstruction=true)
public class FeatureBucketsStoreSamza extends FeatureBucketsMongoStore {
	
	private static final Logger logger = Logger.getLogger(FeatureBucketsStoreSamza.class);
	
	private static final String STORE_NAME_PROPERTY = "fortscale.feature.buckets.store.name";

	private KeyValueStore<String, FeatureBucket> featureBucketStore;
	
	@Autowired
	private DataSourcesSyncTimer dataSourcesSyncTimer;
	
	@Autowired
	private FeatureBucketMetadataRepository featureBucketMetadataRepository;
	
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	
	@Value("${fortscale.aggregation.feature.bucket.leveldb.retention.in.event.seconds}")
	private long levelDbRetentionInEventSeconds;
	@Value("${fortscale.aggregation.feature.bucket.leveldb.retention.in.system.seconds}")
	private long levelDbRetentionInSystemSeconds;
	@Value("${fortscale.aggregation.feature.bucket.leveldb.retention.window.in.system.seconds}")
	private long levelDbRetentionWindowUpdateInSystemSeconds;
	
	private long lastLevelDbCleanupSystemEpochTime = 0;
	
	@Value("${fortscale.aggregation.feature.bucket.store.sync.threshold.in.event.seconds}")
	private long storeSyncThresholdInEventSeconds;
	@Value("${fortscale.aggregation.feature.bucket.store.sync.window.in.system.seconds}")
	private long storeSyncUpdateWindowInSystemSeconds;
	
	private long lastSyncSystemEpochTime = 0;
	
	@SuppressWarnings("unchecked")
	public FeatureBucketsStoreSamza(ExtendedSamzaTaskContext context) {
		Assert.notNull(context);
		Config config = context.getConfig();
		String storeName = getConfigString(config, STORE_NAME_PROPERTY);
		featureBucketStore = (KeyValueStore<String, FeatureBucket>)context.getStore(storeName);
		Assert.notNull(featureBucketStore);
	}
	
	public void cleanup() throws Exception{
		syncAll();
		levelDbCleanup();
	}
	
	private void levelDbCleanup(){
		if(lastLevelDbCleanupSystemEpochTime == 0 || lastLevelDbCleanupSystemEpochTime + levelDbRetentionWindowUpdateInSystemSeconds < System.currentTimeMillis()){
			lastLevelDbCleanupSystemEpochTime = System.currentTimeMillis();
			long lastEventEpochTime = dataSourcesSyncTimer.getLastEventEpochtime();
			//remove from level db those buckets that contains old enough (configured) events and that was synced with mongo before enough (configured) time.
			long endTime = lastEventEpochTime - levelDbRetentionInEventSeconds;
			long syncTime = lastLevelDbCleanupSystemEpochTime - levelDbRetentionInSystemSeconds;
			List<FeatureBucketMetadata> featureBucketMetadataList = featureBucketMetadataRepository.findByEndTimeLessThanAndSyncTimeLessThan(endTime, syncTime);
			for(FeatureBucketMetadata featureBucketMetadata: featureBucketMetadataList){
				featureBucketStore.delete(getBucketKey(featureBucketMetadata.getFeatureBucketConfName(), featureBucketMetadata.getBucketId()));
			}
			featureBucketMetadataRepository.deleteByEndTimeLessThanAndSyncTimeLessThan(endTime, syncTime);
		}
	}
	
	private void syncAll() throws Exception{
		if(lastSyncSystemEpochTime == 0 || lastSyncSystemEpochTime + storeSyncUpdateWindowInSystemSeconds < System.currentTimeMillis()){
			lastSyncSystemEpochTime = System.currentTimeMillis();
			long lastEventEpochTime = dataSourcesSyncTimer.getLastEventEpochtime();
			List<FeatureBucketMetadata> featureBucketMetadataList = featureBucketMetadataRepository.findByisSyncedFalseAndEndTimeLessThan(lastEventEpochTime - storeSyncThresholdInEventSeconds);
			for(FeatureBucketMetadata featureBucketMetadata: featureBucketMetadataList){
				FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketMetadata.getFeatureBucketConfName());
				boolean isSynced = sync(featureBucketConf, featureBucketMetadata.getBucketId());
				if(!isSynced){
					String errorMsg = String.format("failed to sync bucktConfName %s, bucketId %s", featureBucketMetadata.getFeatureBucketConfName(), featureBucketMetadata.getBucketId());
					logger.error(errorMsg);
					throw new RuntimeException(errorMsg);
				}
				featureBucketMetadata.setSynced(true);
				featureBucketMetadata.setSyncTime(lastSyncSystemEpochTime);
				featureBucketMetadataRepository.save(featureBucketMetadata);
			}
		}
	}
	
	private boolean sync(FeatureBucketConf featureBucketConf, String bucketId) throws Exception{
		boolean ret = false;
		String key = getBucketKey(featureBucketConf.getName(), bucketId);
		FeatureBucket featureBucket = featureBucketStore.get(key);
		if(featureBucket != null){
			ret = true;
			super.storeFeatureBucket(featureBucketConf, featureBucket);
			if(featureBucket.getId() == null){
				// At the first time the bucket is stored in mongo it gets an id, so we
				// need to get the updated bucket with the id and store it in the level db so next time we will update the existing document and not insert new document.
				featureBucket = super.getFeatureBucket(featureBucketConf, featureBucket.getBucketId());
			}
			featureBucketStore.put(key, featureBucket);
		}
		return ret;
	}


	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {		
		List<FeatureBucketMetadata> featureBucketMetadataList = featureBucketMetadataRepository.updateFeatureBucketsEndTime(featureBucketConf.getName(), strategyId, newCloseTime);
		if(featureBucketMetadataList.isEmpty()){
			return Collections.emptyList();
		}
		
		List<FeatureBucket> ret = new ArrayList<>();
		for(FeatureBucketMetadata featureBucketMetadata: featureBucketMetadataList){
			String key = getBucketKey(featureBucketMetadata.getFeatureBucketConfName(), featureBucketMetadata.getBucketId());
			FeatureBucket featureBucketSamza = featureBucketStore.get(key);
			featureBucketSamza.setEndTime(newCloseTime);
			featureBucketStore.put(key, featureBucketSamza);
			ret.add(featureBucketSamza);
		}
		return ret;
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId) {
		FeatureBucket ret = featureBucketStore.get(getBucketKey(featureBucketConf.getName(), bucketId));
		if(ret == null){
			ret = super.getFeatureBucket(featureBucketConf, bucketId);
		}
		
		return ret;
	}

	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		String key = getBucketKey(featureBucket);
		FeatureBucket oldFeatureBucket = featureBucketStore.get(key);
		
		if(oldFeatureBucket == null && featureBucket.getId() == null){
			storeFeatureBucketForTheFirstTime(featureBucketConf, featureBucket);
		} else if(featureBucket.getId() != null || featureBucket.getEndTime() < dataSourcesSyncTimer.getLastEventEpochtime()){
			updateFeatureBucketAfterEndTimeReached(featureBucketConf, featureBucket);
		} else{
			updateFeatureBucketBeforeEndTimeReached(featureBucketConf, featureBucket);
		}
	}
	
	private void storeFeatureBucketForTheFirstTime(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		FeatureBucketMetadata featureBucketMetadata = new FeatureBucketMetadata(featureBucket);
		featureBucketMetadataRepository.save(featureBucketMetadata);
		
		String key = getBucketKey(featureBucket);
		featureBucketStore.put(key, featureBucket);
	}
	
	private void updateFeatureBucketBeforeEndTimeReached(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket){
		String key = getBucketKey(featureBucket);
		featureBucketStore.put(key, featureBucket);
	}
	
	private void updateFeatureBucketAfterEndTimeReached(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		super.storeFeatureBucket(featureBucketConf, featureBucket);
		if(featureBucket.getId() == null){
			// At the first time the bucket is stored in mongo it gets an id, so we
			// need to get the updated bucket with the id and store it in the level db so next time we will update the existing document and not insert new document.
			featureBucket = super.getFeatureBucket(featureBucketConf, featureBucket.getBucketId());
		}
		String key = getBucketKey(featureBucket);
		FeatureBucket old = featureBucketStore.get(key);
		if(old != null){
			featureBucketStore.put(key, featureBucket);
		}
	}

	private String getBucketKey(FeatureBucket featureBucket) {
		return getBucketKey(featureBucket.getFeatureBucketConfName(), featureBucket.getBucketId());
	}

	private String getBucketKey(String featureBucketConfName, String bucketId) {
		return featureBucketConfName + "." + bucketId;
	}
}
