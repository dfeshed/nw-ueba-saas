package fortscale.aggregation.feature.bucket.strategy;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.query.Query;

public class FeatureBucketStrategyMongoStore implements FeatureBucketStrategyStore, InitializingBean {
	private static final String COLLECTION_NAME = "feature_bucket_strategies";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyContextId, long latestStartTime) {
		Query query = new Query(where(FeatureBucketStrategyData.STRATEGY_CONTEXT_ID_FIELD).is(strategyContextId).and(FeatureBucketStrategyData.START_TIME_FIELD).lte(latestStartTime));
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, FeatureBucketStrategyData.START_TIME_FIELD);
		query.with(pageable);
		return mongoTemplate.findOne(query, FeatureBucketStrategyData.class, COLLECTION_NAME);
	}

	@Override
	public void storeFeatureBucketStrategyData(FeatureBucketStrategyData featureBucketStrategyData) {
		mongoTemplate.save(featureBucketStrategyData, COLLECTION_NAME);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
			mongoTemplate.createCollection(COLLECTION_NAME);
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index().on(FeatureBucketStrategyData.STRATEGY_CONTEXT_ID_FIELD,Direction.DESC).unique(Duplicates.DROP));
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index().on(FeatureBucketStrategyData.STRATEGY_CONTEXT_ID_FIELD,Direction.DESC).on(FeatureBucketStrategyData.START_TIME_FIELD,Direction.DESC));
		}
	}
}
