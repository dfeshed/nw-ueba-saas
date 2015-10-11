package fortscale.aggregation.feature.bucket.strategy;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fortscale.aggregation.util.MongoDbUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class FeatureBucketStrategyMongoStore implements FeatureBucketStrategyStore, InitializingBean {
	private static final String COLLECTION_NAME = "feature_bucket_strategies";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtils mongoDbUtils;

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime) {
		Query query = new Query(where(FeatureBucketStrategyData.STRATEGY_EVENT_CONTEXT_ID_FIELD).is(strategyEventContextId).and(FeatureBucketStrategyData.START_TIME_FIELD).lte(latestStartTime));
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
		if (!mongoDbUtils.collectionExists(COLLECTION_NAME)) {
			mongoDbUtils.createCollection(COLLECTION_NAME);
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index().on(FeatureBucketStrategyData.STRATEGY_EVENT_CONTEXT_ID_FIELD,Direction.DESC));
			mongoTemplate.indexOps(COLLECTION_NAME).ensureIndex(new Index().on(FeatureBucketStrategyData.STRATEGY_EVENT_CONTEXT_ID_FIELD,Direction.DESC).on(FeatureBucketStrategyData.START_TIME_FIELD,Direction.DESC));
		}
	}

	@Override
	public FeatureBucketStrategyData getLatestFeatureBucketStrategyData(String strategyEventContextId, long latestStartTime, Map<String, String> contextMap) {
		Criteria criteria = where(FeatureBucketStrategyData.STRATEGY_EVENT_CONTEXT_ID_FIELD).is(strategyEventContextId).and(FeatureBucketStrategyData.START_TIME_FIELD).lte(latestStartTime);
		Iterator<Entry<String,String>> iter = contextMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,String> entry = iter.next();
			criteria.and(FeatureBucketStrategyData.getContextNameField(entry.getKey())).is(entry.getValue());
		}
		Query query = new Query(criteria);
		
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, FeatureBucketStrategyData.START_TIME_FIELD);
		query.with(pageable);
		return mongoTemplate.findOne(query, FeatureBucketStrategyData.class, COLLECTION_NAME);
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyDataContainsEventTime(String strategyEventContextId,	long eventTime) {
		Query query = new Query(where(FeatureBucketStrategyData.STRATEGY_EVENT_CONTEXT_ID_FIELD).is(strategyEventContextId).and(FeatureBucketStrategyData.START_TIME_FIELD).lte(eventTime).and(FeatureBucketStrategyData.END_TIME_FIELD).gt(eventTime));
		Sort sort = new Sort(Direction.DESC, FeatureBucketStrategyData.START_TIME_FIELD);
		query.with(sort);
		return mongoTemplate.find(query, FeatureBucketStrategyData.class, COLLECTION_NAME);
	}
}
