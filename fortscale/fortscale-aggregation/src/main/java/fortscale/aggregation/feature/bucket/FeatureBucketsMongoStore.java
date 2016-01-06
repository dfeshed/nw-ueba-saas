package fortscale.aggregation.feature.bucket;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.time.TimestampUtils;


public class FeatureBucketsMongoStore implements FeatureBucketsStore{
	private static final String COLLECTION_NAME_PREFIX = "aggr_";
	private static final int EXPIRE_AFTER_SECONDS_DEFAULT = 90*24*3600;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoDbUtilService.collectionExists(collectionName)) {
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

	public List<FeatureBucket> getFeatureBucketsByTimeRange(FeatureBucketConf featureBucketConf, Long bucketStartTime, Long bucketEndTime, Pageable pageable) {
		String collectionName = getCollectionName(featureBucketConf);

		List<FeatureBucket> featureBuckets = new ArrayList<>();

		if (mongoTemplate.collectionExists(collectionName)) {
			Criteria bucketStartTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).gt(TimestampUtils.convertToSeconds(bucketStartTime));

			Criteria bucketEndTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(bucketEndTime));

			Query query = new Query(bucketStartTimeCriteria.andOperator(bucketEndTimeCriteria));

			if(pageable != null){
				query.with(pageable);
			}
			featureBuckets = mongoTemplate.find(query, FeatureBucket.class, collectionName);
		}

		return featureBuckets;
	}

	private Criteria createContextCriteria(String contextType, String contextName) {
		Map<String, String> contextMap = new HashMap<>(1);
		contextMap.put(contextType, contextName);
		return Criteria.where(FeatureBucket.CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD).in(contextMap); // TODO check for multiple context
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId) {
		String collectionName = getCollectionName(featureBucketConf);
		if (mongoDbUtilService.collectionExists(collectionName)) {
			Query query = new Query(Criteria.where(FeatureBucket.BUCKET_ID_FIELD).is(bucketId));
			
			return mongoTemplate.findOne(query, FeatureBucket.class, collectionName);
		}
		return null;
	}
	
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		String collectionName = getCollectionName(featureBucketConf);
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Bucket ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.BUCKET_ID_FIELD, Direction.DESC).unique(Duplicates.DROP));

			// Strategy ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.STRATEGY_ID_FIELD, Direction.DESC));

			// Start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// end time + start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.END_TIME_FIELD, Direction.ASC)
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// TTL on CreatedAt
			int expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ? featureBucketConf.getExpireAfterSeconds() : EXPIRE_AFTER_SECONDS_DEFAULT;
			
			mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
					.expire(expireAfterSeconds, TimeUnit.SECONDS)
					.named(FeatureBucket.CREATED_AT_FIELD_NAME)
					.on(FeatureBucket.CREATED_AT_FIELD_NAME, Direction.ASC));
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
