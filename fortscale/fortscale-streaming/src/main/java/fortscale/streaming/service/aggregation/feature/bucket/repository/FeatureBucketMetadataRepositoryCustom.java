package fortscale.streaming.service.aggregation.feature.bucket.repository;

import java.util.List;

public interface FeatureBucketMetadataRepositoryCustom {

	public List<FeatureBucketMetadata> updateFeatureBucketsEndTime(String featureBucketConfName, String strategyId, long newCloseTime);
	public List<FeatureBucketMetadata> findByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime);
	public void deleteByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime);
	public void updateByisSyncedFalseAndEndTimeLessThanWithSyncedTrueAndSyncTime(long endTime, long syncTime);
}
