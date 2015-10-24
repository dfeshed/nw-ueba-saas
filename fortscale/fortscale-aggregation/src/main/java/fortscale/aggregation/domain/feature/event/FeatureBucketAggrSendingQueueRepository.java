package fortscale.aggregation.domain.feature.event;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureBucketAggrSendingQueueRepository extends MongoRepository<FeatureBucketAggrSendingQueue, String>, FeatureBucketAggrSendingQueueRepositoryCustom{
	public List<FeatureBucketAggrSendingQueue> findByFireTimeLessThan(Long fireTime);
	public List<FeatureBucketAggrSendingQueue> findByFireTimeLessThan(Long fireTime, Sort sort);
}
