package fortscale.aggregation.feature.bucket;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.time.TimeRange;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class FeatureBucketStoreMongoImpl implements FeatureBucketStore {
	private static final Logger logger = Logger.getLogger(FeatureBucketStoreMongoImpl.class);
	private static final String COLLECTION_NAME_PREFIX = "aggr_";
	private static final int EXPIRE_AFTER_SECONDS_DEFAULT = 90 * 24 * 3600;

	private MongoTemplate mongoTemplate;
	private MongoDbUtilService mongoDbUtilService;
	private StatsService statsService;
	private Map<String, FeatureBucketStoreMetrics> featureBucketConfNameToStoreMetricsMap = new HashMap<>();

	public FeatureBucketStoreMongoImpl(MongoTemplate mongoTemplate, MongoDbUtilService mongoDbUtilService, StatsService statsService) {
		this.mongoTemplate = mongoTemplate;
		this.mongoDbUtilService = mongoDbUtilService;
		this.statsService = statsService;
	}

	@Override
	public Set<String> getDistinctContextIds(FeatureBucketConf featureBucketConf, TimeRange timeRange) {
		Query query = new Query(where(FeatureBucket.START_TIME_FIELD)
				.gte(timeRange.getStart().getEpochSecond())
				.lt(timeRange.getEnd().getEpochSecond()));

		List<?> distinctContextIds = mongoTemplate
				.getCollection(getCollectionName(featureBucketConf))
				.distinct(FeatureBucket.CONTEXT_ID_FIELD, query.getQueryObject());

		return distinctContextIds.stream().map(Object::toString).collect(Collectors.toSet());
	}

	@Override
	public List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String featureBucketConfName, TimeRange timeRange) {
		Aggregation aggregation = Aggregation.newAggregation(
				match(where(FeatureBucket.START_TIME_FIELD).gte(timeRange.getStart().getEpochSecond()).lt(timeRange.getEnd().getEpochSecond())),
				group(FeatureBucket.CONTEXT_ID_FIELD).count().as(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD),
				project(ContextIdToNumOfItems.TOTAL_NUM_OF_ITEMS_FIELD).and("_id").as(ContextIdToNumOfItems.CONTEXT_ID_FIELD).andExclude("_id")
		);

		return mongoTemplate.aggregate(aggregation, getCollectionName(featureBucketConfName), ContextIdToNumOfItems.class).getMappedResults();
	}

	@Override
	public List<FeatureBucket> getFeatureBuckets(String featureBucketConfName, Set<String> contextIds, TimeRange timeRange, int skip, int limit) {
		Query query = new Query(where(FeatureBucket.CONTEXT_ID_FIELD).in(contextIds))
				.addCriteria(where(FeatureBucket.START_TIME_FIELD)
						.gte(timeRange.getStart().getEpochSecond())
						.lt(timeRange.getEnd().getEpochSecond()))
				.skip(skip)
				.limit(limit);

		return mongoTemplate.find(query, FeatureBucket.class, getCollectionName(featureBucketConfName));
	}

	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) {
		String collectionName = ensureCollectionExists(featureBucketConf);

		try {
			mongoTemplate.save(featureBucket, collectionName);
		} catch (Exception e) {
			logger.error("Could not store Feature Bucket {} in Mongo DB collection {}.", featureBucket, collectionName, e);
		}
	}

	private FeatureBucketStoreMetrics getFeatureBucketStoreMetrics(FeatureBucketConf featureBucketConf) {
		String featureBucketConfName = featureBucketConf.getName();

		if (!featureBucketConfNameToStoreMetricsMap.containsKey(featureBucketConfName)) {
			featureBucketConfNameToStoreMetricsMap.put(
					featureBucketConfName,
					new FeatureBucketStoreMetrics(statsService, "Mongo", featureBucketConfName)
			);
		}

		return featureBucketConfNameToStoreMetricsMap.get(featureBucketConfName);
	}

	private String ensureCollectionExists(FeatureBucketConf featureBucketConf) {
		String collectionName = getCollectionName(featureBucketConf);

		if (!mongoDbUtilService.collectionExists(collectionName)) {
			mongoDbUtilService.createCollection(collectionName);

			// Start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// Context ID + start time
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.CONTEXT_ID_FIELD, Direction.ASC)
					.on(FeatureBucket.START_TIME_FIELD, Direction.ASC));

			// Bucket ID
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.BUCKET_ID_FIELD, Direction.ASC)
					.unique());

			int expireAfterSeconds = featureBucketConf.getExpireAfterSeconds() != null ?
					featureBucketConf.getExpireAfterSeconds() :
					EXPIRE_AFTER_SECONDS_DEFAULT;

			mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
					.on(FeatureBucket.CREATED_AT_FIELD_NAME, Direction.ASC)
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
