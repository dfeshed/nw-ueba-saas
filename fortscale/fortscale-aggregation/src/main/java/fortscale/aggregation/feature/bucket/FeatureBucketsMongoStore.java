package fortscale.aggregation.feature.bucket;

import com.mongodb.WriteResult;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.FIndex;
import fortscale.utils.monitoring.stats.StatsService;
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
	private static final Logger logger = Logger.getLogger(FeatureBucketsMongoStore.class);

	private static final String COLLECTION_NAME_PREFIX = "aggr_";
	private static final String COLLECTIONS_NAMES_DELIMTER =",";

	private static final int EXPIRE_AFTER_SECONDS_DEFAULT = 90*24*3600;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	@Autowired
	private StatsService statsService;

	//TODO: remove this after we test it and see that it works as we expected
	@Value("${fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore.getFeatureBucketsWithSpecificFieldProjectionByContextIdAndTimeRange.use.projection:false}")
	boolean useProjection;

	@Value("${fortscale.store.collection.backup.prefix}")
	private String collectionsBackupPrefixListAsString;

	private Map<FeatureBucketConf, FeatureBucketsStoreMetrics> featureBucketConfToMetric = new HashMap<>();

	private FeatureBucketsStoreMetrics getMetrics(FeatureBucketConf featureBucketConf) {
		if (!featureBucketConfToMetric.containsKey(featureBucketConf)) {
			featureBucketConfToMetric.put(featureBucketConf,
					new FeatureBucketsStoreMetrics(statsService, "mongo", featureBucketConf));
		}
		return featureBucketConfToMetric.get(featureBucketConf);
	}

	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {
		String collectionName = getCollectionName(featureBucketConf);
		FeatureBucketsStoreMetrics metrics = getMetrics(featureBucketConf);

		if (mongoDbUtilService.collectionExists(collectionName)) {
			Update update = new Update();
			update.set(FeatureBucket.END_TIME_FIELD, newCloseTime);
			Query query = new Query(Criteria.where(FeatureBucket.STRATEGY_ID_FIELD).is(strategyId));
			
			WriteResult writeResult = mongoTemplate.updateMulti(query, update, FeatureBucket.class, collectionName);
			metrics.updateFeatureBucketsCalls++;

			if(writeResult.getN()>0){
				metrics.updatedFeatureBuckets += writeResult.getN();
				return mongoTemplate.find(query, FeatureBucket.class, collectionName);
			} else{
				return Collections.emptyList();
			}
		} else {
			metrics.updateFeatureBucketsFailures++;
		}

		return Collections.emptyList();
	}

	@Override
	public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime) {
		List<FeatureBucket> result = new ArrayList<>();


		List<String> collectionNames = getCollectionNames(featureBucketConf);

		Criteria bucketStartTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(bucketStartTime));

		Criteria bucketEndTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).lt(TimestampUtils.convertToSeconds(bucketEndTime));

		Criteria contextCriteria = createContextCriteria(contextType, ContextName);

		Query query = new Query(bucketStartTimeCriteria.andOperator(bucketEndTimeCriteria,contextCriteria));

		//Get the data from the origin collection
		result = readFromMongo(collectionNames,query,featureBucketConf);

		return result;



	}

	private List<String> getCollectionNames(FeatureBucketConf featureBucketConf) {
		List<String> collectionNames = new ArrayList<>();

		//In case of backups collections - will create the backup prefix list
		List<String> collectionsBackupPrefixList = ConversionUtils.convertStringToList(collectionsBackupPrefixListAsString, COLLECTIONS_NAMES_DELIMTER);


		String coreName = String.format("%s%s", COLLECTION_NAME_PREFIX, featureBucketConf.getName());
		collectionNames.add(coreName);

		collectionsBackupPrefixList.forEach(prefix->{
			if(!org.apache.commons.lang.StringUtils.isEmpty(prefix))
				collectionNames.add(prefix + coreName);
		});

		return collectionNames;

	}

	private List<FeatureBucket> readFromMongo (List<String> collectionNames,Query query,FeatureBucketConf featureBucketConf) {
		FeatureBucketsStoreMetrics metrics = getMetrics(featureBucketConf);
		List<FeatureBucket> featureBuckets = new ArrayList<>();


			collectionNames.forEach(collection->{
				try {
					featureBuckets.addAll(mongoTemplate.find(query, FeatureBucket.class, collection));
					metrics.retrieveFeatureBucketsCalls++;
					metrics.retrievedFeatureBuckets += featureBuckets.size();
				}
				catch (Exception e) {
					metrics.retrieveFeatureBucketsFailures++;
					throw new RuntimeException("Could not fetch feature buckets from collection " + collection + " due to: " + e);
				}
			});

			return featureBuckets;
	}


	public List<FeatureBucket> getFeatureBucketsByEndTimeBetweenTimeRange(FeatureBucketConf featureBucketConf, Long bucketStartTime, Long bucketEndTime, Pageable pageable) {
		List<FeatureBucket> result = new ArrayList<>();

		Criteria bucketStartTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).gt(TimestampUtils.convertToSeconds(bucketStartTime));

		Criteria bucketEndTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(bucketEndTime));

		Query query = new Query(bucketStartTimeCriteria.andOperator(bucketEndTimeCriteria));


		if(pageable != null) {
			query.with(pageable);
		}

		List<String> collectionNames = getCollectionNames(featureBucketConf);

		result = readFromMongo(collectionNames,query,featureBucketConf);

		return result;
	}

	private Criteria createContextCriteria(String contextType, String contextName) {
		Map<String, String> contextMap = new HashMap<>(1);
		contextMap.put(contextType, contextName);
		String contextId = FeatureBucketUtils.buildContextId(contextMap);
		return Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).is(contextId);
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId) {
		List<FeatureBucket> result = new ArrayList<>();

		List<String> collectionNames = getCollectionNames(featureBucketConf);

		Query query = new Query(Criteria.where(FeatureBucket.BUCKET_ID_FIELD).is(bucketId));

		result = readFromMongo(collectionNames,query,featureBucketConf);

		return result.get(0);

	}
	
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception{
		int expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ? featureBucketConf.getExpireAfterSeconds() : EXPIRE_AFTER_SECONDS_DEFAULT;
		String collectionName = createCollectionIfNotExist(featureBucketConf, expireAfterSeconds);
		FeatureBucketsStoreMetrics metrics = getMetrics(featureBucketConf);
		try {
			metrics.saveFeatureBucketsCalls++;
			mongoTemplate.save(featureBucket, collectionName);
		} catch (Exception e) {
			metrics.saveFeatureBucketsFailures++;
			throw new Exception("Got exception while trying to save featureBucket to mongodb. featureBucket: "+featureBucket.toString(), e);
		}
	}
	public void insertFeatureBuckets(FeatureBucketConf featureBucketConf, Collection<FeatureBucket> featureBuckets) throws Exception{
		// TTL on CreatedAt
		int expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ? featureBucketConf.getExpireAfterSeconds() : EXPIRE_AFTER_SECONDS_DEFAULT;
		String collectionName = createCollectionIfNotExist(featureBucketConf, expireAfterSeconds);

		FeatureBucketsStoreMetrics metrics = getMetrics(featureBucketConf);
		try {
			mongoTemplate.insert(featureBuckets, collectionName);
			metrics.insertFeatureBucketsCalls++;
		} catch (Exception e) {
			metrics.insertFeatureBucketsFailures++;
			throw new Exception("Got exception while trying to save featureBuckets to mongodb. featureBuckets = "+featureBuckets.toString(), e);
		}
	}



	private String createCollectionIfNotExist(FeatureBucketConf featureBucketConf, int expireAfterSeconds) {
		String collectionName = getCollectionName(featureBucketConf);
		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Bucket ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.BUCKET_ID_FIELD, Direction.DESC).unique(Duplicates.DROP));

			// Context ID + start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.CONTEXT_ID_FIELD, Direction.ASC)
					.on(FeatureBucket.START_TIME_FIELD, Direction.DESC));

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
			getMetrics(featureBucketConf).collectionCreations++;
		}
		return collectionName;
	}

	@SuppressWarnings("unchecked")
	public List<String> findDistinctContextByTimeRange(FeatureBucketConf featureBucketConf, Long startTime, Long endTime){
		Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));

		Criteria endTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).lt(TimestampUtils.convertToSeconds(endTime));

		Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));
		List res = mongoTemplate.getCollection(getCollectionName(featureBucketConf)).distinct(FeatureBucket.CONTEXT_ID_FIELD, query.getQueryObject());
		FeatureBucketsStoreMetrics metrics = getMetrics(featureBucketConf);
		metrics.retrieveContextsCalls++;
		metrics.retrievedContexts += res.size();
		return res;
	}

	private String getCollectionName(FeatureBucketConf featureBucketConf) {
		return String.format("%s%s", COLLECTION_NAME_PREFIX, featureBucketConf.getName());
	}

	public List<FeatureBucket> getFeatureBucketsWithSpecificFieldProjectionByContextIdAndTimeRange(FeatureBucketConf featureBucketConf,
																								   String contextId,
																								   long startTimeInSeconds,
																								   long endTimeInSeconds,
																								   String fieldName,
																								   boolean fieldMustExist,
																								   List<String> additionalFieldsToInclude) {
		List<FeatureBucket> result = new ArrayList<>();

		List<String> collectionNames = getCollectionNames(featureBucketConf);

		Criteria contextIdCriteria = Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).is(contextId);
		Criteria startTimeInSecondsCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(startTimeInSeconds);
		Criteria endTimeInSecondsCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(endTimeInSeconds);
		Query query = new Query(contextIdCriteria.andOperator(startTimeInSecondsCriteria, endTimeInSecondsCriteria));

		if(useProjection) {
			if(fieldMustExist) {
				query.addCriteria(Criteria.where(fieldName).exists(true));
			}
			query.fields().include(FeatureBucket.CONTEXT_ID_FIELD);
			query.fields().include(FeatureBucket.START_TIME_FIELD);
			query.fields().include(FeatureBucket.END_TIME_FIELD);
			query.fields().include(fieldName);

			if(additionalFieldsToInclude!=null) {
				additionalFieldsToInclude.forEach(field -> query.fields().include(field));
			}
		}


		result = readFromMongo(collectionNames,query,featureBucketConf);



		return result;
	}
}
