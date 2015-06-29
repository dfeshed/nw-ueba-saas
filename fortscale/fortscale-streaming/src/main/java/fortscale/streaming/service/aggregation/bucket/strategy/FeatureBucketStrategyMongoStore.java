package fortscale.streaming.service.aggregation.bucket.strategy;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class FeatureBucketStrategyMongoStore implements	FeatureBucketStrategyStore {
	
	public static final String STRATEGY_CONTEXT_ID_FIELD = "strategyContextId";
	public static final String START_TIME_FIELD = "startTime";
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime) {
		Query query = new Query(where(STRATEGY_CONTEXT_ID_FIELD).is(strategyContextId).and(START_TIME_FIELD).lte(latestStartTime));
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, START_TIME_FIELD);
		query.with(pageable);
		return mongoTemplate.findOne(query, FeatureBucketStrategyData.class);
	}

}
