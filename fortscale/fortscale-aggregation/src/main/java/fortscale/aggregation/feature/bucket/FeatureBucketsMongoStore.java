package fortscale.aggregation.feature.bucket;

import com.mongodb.WriteResult;
import fortscale.aggregation.util.MongoDbUtils;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;


public class FeatureBucketsMongoStore implements FeatureBucketsStore{
	private static final String COLLECTION_NAME_PREFIX = "aggr_";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtils mongoDbUtils;

	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoDbUtils.collectionExists(collectionName)) {
			Update update = new Update();
			update.set(FeatureBucket.END_TIME_FIELD, newCloseTime);
			Query query = new Query(Criteria.where(FeatureBucket.STRATEGY_ID_FIELD).is(strategyId));
			
			WriteResult writeResult = mongoTemplate.updateMulti(query, update, FeatureBucket.class, collectionName);
			
			if(writeResult.getN()>0){
				return mongoTemplate.find(query, FeatureBucket.class, collectionName);
			} else{
				return Collections.emptyList();
			}
		}

		return Collections.emptyList();
	}

	@Override
	public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoTemplate.collectionExists(collectionName)) {
			Criteria bucketStartTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(bucketStartTime));

			Criteria bucketEndTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).lt(TimestampUtils.convertToSeconds(bucketEndTime));

			Criteria contextCriteria = createContextCriteria(contextType, ContextName);

			Query query = new Query(bucketStartTimeCriteria.andOperator(bucketEndTimeCriteria,contextCriteria));

			List<FeatureBucket> featureBuckets = mongoTemplate.find(query, FeatureBucket.class, collectionName);
			return featureBuckets;
		}
		else {
			throw new RuntimeException("Could not fetch feature buckets from collection " + collectionName);
		}
	}

	private Criteria createContextCriteria(String contextType, String contextName) {
		Map<String, String> contextMap = new HashMap<>(1);
		contextMap.put(contextType, contextName);
		return Criteria.where(FeatureBucket.CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD).in(contextMap); // TODO check for multiple context
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId) {
		String collectionName = getCollectionName(featureBucketConf);
		if (mongoDbUtils.collectionExists(collectionName)) {
			Query query = new Query(Criteria.where(FeatureBucket.BUCKET_ID_FIELD).is(bucketId));
			
			return mongoTemplate.findOne(query, FeatureBucket.class, collectionName);
		}
		return null;
	}
	
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		String collectionName = getCollectionName(featureBucketConf);
		if (!mongoDbUtils.collectionExists(collectionName)) {
			mongoDbUtils.createCollection(collectionName);

			// Bucket ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.BUCKET_ID_FIELD, Direction.DESC).unique(Duplicates.DROP));

			// Strategy ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.STRATEGY_ID_FIELD, Direction.DESC));

			// Start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

		}
		try {
			mongoTemplate.save(featureBucket, collectionName);
		} catch (Exception e) {
			throw new Exception("Got exception while trying to save featureBucket to mongodb. featureBucket: "+featureBucket.toString(), e);
		}
	}

	private String getCollectionName(FeatureBucketConf featureBucketConf) {
		return String.format("%s%s", COLLECTION_NAME_PREFIX, featureBucketConf.getName());
	}

}
