package fortscale.aggregation.domain.feature.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class FeatureBucketAggrSendingQueueRepositoryImpl implements FeatureBucketAggrSendingQueueRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void deleteByFireTimeLessThan(Long fireTime) {
		Query query = new Query(Criteria.where(FeatureBucketAggrSendingQueue.FIRE_TIME_FIELD).lt(fireTime));
		mongoTemplate.remove(query,  FeatureBucketAggrSendingQueue.class);
	}

}
