package fortscale.aggregation.feature.bucket;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.time.TimeRange;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A Mongo based {@link FeatureBucketStore}.
 */
public class FeatureBucketStoreMongoImpl implements FeatureBucketStore {
	private static final Logger logger = Logger.getLogger(FeatureBucketStoreMongoImpl.class);
	public static final String COLLECTION_NAME_PREFIX = "aggr_";

	private MongoTemplate mongoTemplate;
	private MongoDbUtilService mongoDbUtilService;
	private long defaultExpireAfterSeconds;

	/**
	 * C'tor.
	 *
	 * @param mongoTemplate             the {@link MongoTemplate}
	 * @param mongoDbUtilService        the {@link MongoDbUtilService}
	 * @param defaultExpireAfterSeconds the default TTL of the {@link FeatureBucket} documents
	 */
	public FeatureBucketStoreMongoImpl(
			MongoTemplate mongoTemplate, MongoDbUtilService mongoDbUtilService, long defaultExpireAfterSeconds) {

		this.mongoTemplate = mongoTemplate;
		this.mongoDbUtilService = mongoDbUtilService;
		this.defaultExpireAfterSeconds = defaultExpireAfterSeconds;
	}

	/**
	 * @see FeatureBucketReader#getDistinctContextIds(FeatureBucketConf, TimeRange)
	 */
	@Override
	public Set<String> getDistinctContextIds(FeatureBucketConf featureBucketConf, TimeRange timeRange) {
		Query query = new Query(Criteria.where(FeatureBucket.START_TIME_FIELD)
				.gte(Date.from(timeRange.getStart()))
				.lt(Date.from(timeRange.getEnd())));

		List<?> distinctContextIds = mongoTemplate
				.getCollection(getCollectionName(featureBucketConf))
				.distinct(FeatureBucket.CONTEXT_ID_FIELD, query.getQueryObject());

		return distinctContextIds.stream().map(Object::toString).collect(Collectors.toSet());
	}

	/**
	 * @see FeatureBucketReader#getFeatureBuckets(String, Set, TimeRange)
	 */
	@Override
	public List<FeatureBucket> getFeatureBuckets(
			String featureBucketConfName, Set<String> contextIds, TimeRange timeRange) {
		Query query = new Query()
				.addCriteria(Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).in(contextIds))
				.addCriteria(Criteria.where(FeatureBucket.START_TIME_FIELD).gte(timeRange.getStart()).lt(timeRange.getEnd()));
		return mongoTemplate.find(query, FeatureBucket.class, getCollectionName(featureBucketConfName));
	}

	@Override
	public List<FeatureBucket> getFeatureBuckets(String featureBucketConfName, String contextIds, TimeRange timeRange) {
		Query query = new Query()
				.addCriteria(Criteria.where(FeatureBucket.CONTEXT_ID_FIELD).is(contextIds))
				.addCriteria(Criteria.where(FeatureBucket.START_TIME_FIELD).gte(timeRange.getStart()).lt(timeRange.getEnd()));
		return mongoTemplate.find(query, FeatureBucket.class, getCollectionName(featureBucketConfName));
	}

	/**
	 * @see FeatureBucketStore#storeFeatureBucket(FeatureBucketConf, FeatureBucket)
	 */
	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) {
		String collectionName = ensureCollectionExists(featureBucketConf);

		try {
			mongoTemplate.save(featureBucket, collectionName);
		} catch (Exception e) {
			logger.error("Failed storing Feature Bucket {} in Mongo collection {}.", featureBucket, collectionName, e);
		}
	}

	private String ensureCollectionExists(FeatureBucketConf featureBucketConf) {
		String collectionName = getCollectionName(featureBucketConf);

		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Start time index
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// Context ID + start time index
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.CONTEXT_ID_FIELD, Direction.ASC)
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// Bucket ID (unique)
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.BUCKET_ID_FIELD, Direction.ASC)
					.unique());

			long expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ?
					featureBucketConf.getExpireAfterSeconds() :
					defaultExpireAfterSeconds;

			// Created at (TTL)
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.CREATED_AT_FIELD, Direction.ASC)
					.expire(expireAfterSeconds, TimeUnit.SECONDS));
		}

		return collectionName;
	}

	private static String getCollectionName(String featureBucketConfName) {
		return COLLECTION_NAME_PREFIX + featureBucketConfName;
	}

	private static String getCollectionName(FeatureBucketConf featureBucketConf) {
		return getCollectionName(featureBucketConf.getName());
	}
}
