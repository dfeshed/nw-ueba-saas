package fortscale.streaming.service.aggregation.feature.bucket.repository;

import java.util.List;

public interface FeatureBucketMetadataRepositoryCustom {

	public void updateFeatureBucketsEndTime(String bucketName, String strategyId, long newCloseTime);
	public List<FeatureBucketMetadata> findByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime);
}
