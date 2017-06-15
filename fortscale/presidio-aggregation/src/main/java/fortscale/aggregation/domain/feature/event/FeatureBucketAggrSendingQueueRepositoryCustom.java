package fortscale.aggregation.domain.feature.event;

public interface FeatureBucketAggrSendingQueueRepositoryCustom {

	public void deleteByFireTimeLessThan(Long fireTime);
}
