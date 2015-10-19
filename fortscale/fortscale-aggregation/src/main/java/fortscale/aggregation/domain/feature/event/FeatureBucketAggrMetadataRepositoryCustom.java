package fortscale.aggregation.domain.feature.event;

public interface FeatureBucketAggrMetadataRepositoryCustom {
	public void updateFeatureBucketsEndTime(String bucketConfName, String bucketId, long newCloseTime);
}
