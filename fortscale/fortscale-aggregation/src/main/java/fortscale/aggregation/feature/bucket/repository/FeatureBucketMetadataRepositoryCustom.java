package fortscale.aggregation.feature.bucket.repository;

import java.util.List;

public interface FeatureBucketMetadataRepositoryCustom {
	List<FeatureBucketMetadata> updateFeatureBucketsEndTime(String featureBucketConfName, String strategyId, long newCloseTime);
	List<FeatureBucketMetadata> findByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime);
	void deleteByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime);
	void updateByIsSyncedFalseAndEndTimeLessThanWithSyncedTrueAndSyncTime(long endTime, long syncTime);
}
