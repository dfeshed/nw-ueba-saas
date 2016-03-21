package fortscale.streaming.service.aggregation.feature.bucket;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.feature.bucket.repository.FeatureBucketMetadata;
import fortscale.streaming.service.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import fortscale.utils.logging.Logger;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.*;

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
	
	@Value("${fortscale.aggregation.feature.bucket.keyvaluedb.retention.in.event.seconds}")
	private long keyValueDbRetentionInEventSeconds;
	@Value("${fortscale.aggregation.feature.bucket.keyvaluedb.retention.in.system.seconds}")
	private long keyValueDbRetentionInSystemSeconds;
	@Value("${fortscale.aggregation.feature.bucket.keyvaluedb.retention.window.in.system.seconds}")
	private long keyValueDbRetentionWindowUpdateInSystemSeconds;
	
	private long lastKeyValueDbCleanupSystemEpochTime = 0;
	
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

		keyValueDbCleanup();
	}
	
	private void keyValueDbCleanup(){
		if(lastKeyValueDbCleanupSystemEpochTime == 0 || lastKeyValueDbCleanupSystemEpochTime + keyValueDbRetentionWindowUpdateInSystemSeconds*1000 < System.currentTimeMillis()){
			lastKeyValueDbCleanupSystemEpochTime = System.currentTimeMillis();
			long lastEventEpochTime = dataSourcesSyncTimer.getLastEventEpochtime();
			//remove from level db those buckets that contains old enough (configured) events and that was synced with mongo before enough (configured) time.
			long endTime = lastEventEpochTime - keyValueDbRetentionInEventSeconds;
			long syncTime = lastKeyValueDbCleanupSystemEpochTime - keyValueDbRetentionInSystemSeconds*1000;
			List<FeatureBucketMetadata> featureBucketMetadataList = featureBucketMetadataRepository.findByEndTimeLessThanAndSyncTimeLessThan(endTime, syncTime);
			for(FeatureBucketMetadata featureBucketMetadata: featureBucketMetadataList){
				featureBucketStore.delete(getBucketKey(featureBucketMetadata.getFeatureBucketConfName(), featureBucketMetadata.getBucketId()));
			}
			featureBucketMetadataRepository.deleteByEndTimeLessThanAndSyncTimeLessThan(endTime, syncTime);
		}
	}

	private void syncAll() throws Exception{
		if(lastSyncSystemEpochTime == 0 || lastSyncSystemEpochTime + storeSyncUpdateWindowInSystemSeconds < System.currentTimeMillis()){
			long lastEventEpochTime = dataSourcesSyncTimer.getLastEventEpochtime();
			long endTimeLt = lastEventEpochTime - storeSyncThresholdInEventSeconds;
			List<FeatureBucketMetadata> featureBucketMetadataList = featureBucketMetadataRepository.findByisSyncedFalseAndEndTimeLessThan(endTimeLt);
			Map<String, Collection<FeatureBucket>> bucketConfNameToBucketCollectionMap = new HashMap<>();
			String errorMsg = "";
			boolean error = false;

			// Creating collections of buckets to sync per FeatureBucketConf
			for(FeatureBucketMetadata featureBucketMetadata: featureBucketMetadataList) {
				String featureBucketConfName = featureBucketMetadata.getFeatureBucketConfName();
				Collection<FeatureBucket> featureBuckets = bucketConfNameToBucketCollectionMap.get(featureBucketConfName);
				if (featureBuckets == null) {
					featureBuckets = new ArrayList<>();
					bucketConfNameToBucketCollectionMap.put(featureBucketConfName, featureBuckets);
				}
				String key = getBucketKey(featureBucketConfName, featureBucketMetadata.getBucketId());
				FeatureBucket featureBucket = featureBucketStore.get(key);
				if (featureBucket != null) {
					if(featureBucket.getId() == null) {
						featureBuckets.add(featureBucket);
					}
				} else {
					errorMsg += String.format("\nFailed to sync bucktConfName %s, bucketId %s", featureBucketMetadata.getFeatureBucketConfName(), featureBucketMetadata.getBucketId());
					error = true;
				}
			}

			// Bulk Insert Per Collection
			for(Map.Entry<String, Collection<FeatureBucket>> entry: bucketConfNameToBucketCollectionMap.entrySet()) {
				FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(entry.getKey());
				Collection<FeatureBucket> featureBuckets = entry.getValue();

				insertFeatureBuckets(featureBucketConf, featureBuckets);

				for(FeatureBucket featureBucket: featureBuckets) {
					if(featureBucket.getId()==null) {
						errorMsg += String.format("\nfeatureBucket.getId() is null after inserting the bucket to mongodb: bucktConfName %s, bucketId %s", featureBucketConf.getName(), featureBucket.getBucketId());
						error = true;
					}
					String key = getBucketKey(featureBucketConf.getName(), featureBucket.getBucketId());
					featureBucketStore.put(key, featureBucket);
				}
			}

			featureBucketMetadataRepository.updateByisSyncedFalseAndEndTimeLessThanWithSyncedTrueAndSyncTime(endTimeLt, lastSyncSystemEpochTime);

			if(error){
				logger.error(errorMsg);
				throw new RuntimeException(errorMsg);
			}
		}
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
