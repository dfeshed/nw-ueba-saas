package fortscale.aggregation.feature.bucket;

import com.mongodb.WriteResult;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class FeatureBucketsMongoStore implements FeatureBucketsStore{
	private static final String COLLECTION_NAME_PREFIX = "aggr_";
	private static final int EXPIRE_AFTER_SECONDS_DEFAULT = 90*24*3600;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	//TODO: remove this after we test it and see that it works as we expected
	@Value("${fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore.getFeatureBucketsWithSpecificFieldProjectionByContextIdAndTimeRange.use.projection:false}")
	boolean useProjection;

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

	public List<FeatureBucket> getFeatureBucketsByEndTimeBetweenTimeRange(FeatureBucketConf featureBucketConf, Long bucketStartTime, Long bucketEndTime, Pageable pageable) {
		String collectionName = getCollectionName(featureBucketConf);

		Criteria bucketStartTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).gt(TimestampUtils.convertToSeconds(bucketStartTime));

		Criteria bucketEndTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(bucketEndTime));

		Query query = new Query(bucketStartTimeCriteria.andOperator(bucketEndTimeCriteria));

		if(pageable != null) {
			query.with(pageable);
		}
		List<FeatureBucket> featureBuckets = mongoTemplate.find(query, FeatureBucket.class, collectionName);


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

		Query query = new Query(Criteria.where(FeatureBucket.BUCKET_ID_FIELD).is(bucketId));

		return mongoTemplate.findOne(query, FeatureBucket.class, collectionName);

	}
	
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		String collectionName = getCollectionName(featureBucketConf);
		int expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ? featureBucketConf.getExpireAfterSeconds() : EXPIRE_AFTER_SECONDS_DEFAULT;
		createCollectionIfNotExist(collectionName, expireAfterSeconds);
		try {
			mongoTemplate.save(featureBucket, collectionName);
		} catch (Exception e) {
			throw new Exception("Got exception while trying to save featureBucket to mongodb. featureBucket: "+featureBucket.toString(), e);
		}
	}
	public void insertFeatureBuckets(FeatureBucketConf featureBucketConf, Collection<FeatureBucket> featureBuckets) throws Exception{
		String collectionName = getCollectionName(featureBucketConf);
		// TTL on CreatedAt
		int expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ? featureBucketConf.getExpireAfterSeconds() : EXPIRE_AFTER_SECONDS_DEFAULT;
		createCollectionIfNotExist(collectionName, expireAfterSeconds);

		try {
			mongoTemplate.insert(featureBuckets, collectionName);

		} catch (Exception e) {
			throw new Exception("Got exception while trying to save featureBuckets to mongodb. featureBuckets = "+featureBuckets.toString(), e);
		}
	}



	private void createCollectionIfNotExist(String collectionName, int expireAfterSeconds) {
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Bucket ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.BUCKET_ID_FIELD, Direction.DESC).unique(Duplicates.DROP));

			// Context ID + start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.CONTEXT_ID_FIELD, Direction.ASC)
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// Strategy ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.STRATEGY_ID_FIELD, Direction.DESC));

			// Start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));


			// end time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.END_TIME_FIELD, Direction.ASC));

			mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex()
					.expire(expireAfterSeconds, TimeUnit.SECONDS)
					.named(FeatureBucket.CREATED_AT_FIELD_NAME)
					.on(FeatureBucket.CREATED_AT_FIELD_NAME, Direction.ASC));
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> findDistinctContextByTimeRange(FeatureBucketConf featureBucketConf, Long startTime, Long endTime){
		Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));

		Criteria endTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).lt(TimestampUtils.convertToSeconds(endTime));

		Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));
		return mongoTemplate.getCollection(getCollectionName(featureBucketConf)).distinct(FeatureBucket.CONTEXT_ID_FIELD, query.getQueryObject());
	}

	private String getCollectionName(FeatureBucketConf featureBucketConf) {
		return String.format("%s%s", COLLECTION_NAME_PREFIX, featureBucketConf.getName());
	}

	public List<FeatureBucket> getFeatureBucketsWithSpecificFieldProjectionByContextIdAndTimeRange(FeatureBucketConf featureBucketConf,
																								   String contextId,
																								   long startTimeInSeconds,
																								   long endTimeInSeconds,
																								   String fieldName) {
		String collectionName = getCollectionName(featureBucketConf);

		Criteria contextIdCriteria = Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).is(contextId);
		Criteria startTimeInSecondsCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(startTimeInSeconds);
		Criteria endTimeInSecondsCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(endTimeInSeconds);
		Query query = new Query(contextIdCriteria.andOperator(startTimeInSecondsCriteria, endTimeInSecondsCriteria));
		if(useProjection) {
			query.fields().include(FeatureBucket.CONTEXT_ID_FIELD);
			query.fields().include(FeatureBucket.START_TIME_FIELD);
			query.fields().include(FeatureBucket.END_TIME_FIELD);
			query.fields().include(fieldName);
		}
		return mongoTemplate.find(query, FeatureBucket.class, collectionName);
	}
}
