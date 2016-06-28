package fortscale.aggregation.domain.feature.event;

import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class FeatureBucketAggrSendingQueueRepositoryImpl implements FeatureBucketAggrSendingQueueRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private StatsService statsService;

	private FeatureBucketAggrSendingQueueRepositoryMetrics metrics;

	public FeatureBucketAggrSendingQueueRepositoryImpl() {
		metrics = new FeatureBucketAggrSendingQueueRepositoryMetrics(statsService);
	}

	@Override
	public void deleteByFireTimeLessThan(Long fireTime) {
		Query query = new Query(Criteria.where(FeatureBucketAggrSendingQueue.FIRE_TIME_FIELD).lt(fireTime));
		mongoTemplate.remove(query,  FeatureBucketAggrSendingQueue.class);
		metrics.fireEpochtime = fireTime;
	}

}
