package fortscale.aggregation.domain.feature.event;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureBucketAggrSendingQueueRepository extends MongoRepository<FeatureBucketAggrSendingQueue, String>{
	public List<FeatureBucketAggrSendingQueue> findByFireTimeLessThan(Long fireTime);
}
