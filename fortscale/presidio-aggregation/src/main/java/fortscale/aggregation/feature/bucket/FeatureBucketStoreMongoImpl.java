package fortscale.aggregation.feature.bucket;

import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * A Mongo based {@link FeatureBucketStore}.
 */
public class FeatureBucketStoreMongoImpl implements FeatureBucketStore, StoreManagerAware {
	private static final Logger logger = Logger.getLogger(FeatureBucketStoreMongoImpl.class);
	public static final String COLLECTION_NAME_PREFIX = "aggr_";
	private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

	private MongoTemplate mongoTemplate;
	private StoreManager storeManager;
	private final long selectorPageSize;

	/**
	 * C'tor.
	 * @param mongoTemplate             the {@link MongoTemplate}
	 * @param mongoDbBulkOpUtil
	 */
	public FeatureBucketStoreMongoImpl(
			MongoTemplate mongoTemplate, MongoDbBulkOpUtil mongoDbBulkOpUtil, long selectorPageSize) {

		this.mongoTemplate = mongoTemplate;
		this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
		this.selectorPageSize = selectorPageSize;
	}

	/**
	 * @see FeatureBucketReader#getDistinctContextIds(FeatureBucketConf, TimeRange)
	 */
	@Override
	public Set<String> getDistinctContextIds(FeatureBucketConf featureBucketConf, TimeRange timeRange) {
		String collectionName = getCollectionName(featureBucketConf);

		Date startDate = Date.from(timeRange.getStart());
		Date endDate = Date.from(timeRange.getEnd());
		Set<String> distinctContexts;
		try {
			Query query = new Query(Criteria.where(FeatureBucket.START_TIME_FIELD)
					.gte(startDate)
					.lt(endDate));
			List<?> distinctContextIds = mongoTemplate
					.getCollection(collectionName)
					.distinct(FeatureBucket.CONTEXT_ID_FIELD, query.getQueryObject());
			distinctContexts = distinctContextIds.stream().map(Object::toString).collect(Collectors.toSet());
		} catch (MongoCommandException e) {
			long nextPageIndex = 0;
			Set<String> subList;
			distinctContexts = new HashSet<>();
			do {
				subList = aggregateContextIds(startDate, endDate,
						nextPageIndex * selectorPageSize, selectorPageSize, collectionName, true);
				distinctContexts.addAll(subList);
				nextPageIndex++;
			} while (subList.size() == selectorPageSize);
		}

		logger.debug("found {} distinct contexts", distinctContexts.size());
		return distinctContexts;
	}

	/**
	 * Aggregate distinct contextIds
	 * @param startDate startDate
	 * @param endDate endDate
	 * @param skip skip
	 * @param limit limit
	 * @param collectionName collectionName
	 * @param allowDiskUse allowDiskUse
	 * @return set of distinct contextIds
	 */
	private Set<String> aggregateContextIds(
			Date startDate, Date endDate, long skip, long limit, String collectionName, boolean allowDiskUse) {

		List<AggregationOperation> aggregationOperations = new LinkedList<>();
		aggregationOperations.add(match(where(FeatureBucket.START_TIME_FIELD).gte(startDate).lt(endDate)));

		aggregationOperations.add(group(FeatureBucket.CONTEXT_ID_FIELD));
		aggregationOperations.add(project(FeatureBucket.CONTEXT_ID_FIELD).and("_id").as(FeatureBucket.CONTEXT_ID_FIELD)
				.andExclude("_id"));

		if (skip >= 0 && limit > 0) {
			aggregationOperations.add(sort(Sort.Direction.ASC, FeatureBucket.CONTEXT_ID_FIELD));
			aggregationOperations.add(skip(skip));
			aggregationOperations.add(limit(limit));
		}

		Aggregation aggregation = newAggregation(aggregationOperations).withOptions(Aggregation.newAggregationOptions().
				allowDiskUse(allowDiskUse).build());

		List<DBObject> aggrResult = mongoTemplate
				.aggregate(aggregation, collectionName, DBObject.class)
				.getMappedResults();

		return aggrResult.stream()
				.map(result -> (String) result.get(FeatureBucket.CONTEXT_ID_FIELD))
				.collect(Collectors.toSet());
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
	 * @see FeatureBucketStore#storeFeatureBucket(FeatureBucketConf, List, StoreMetadataProperties)
	 */
	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, List<FeatureBucket> featureBuckets, StoreMetadataProperties storeMetadataProperties) {
		String collectionName = getCollectionName(featureBucketConf);

		try {
			mongoDbBulkOpUtil.insertUnordered(featureBuckets,collectionName);
			storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
		} catch (Exception e) {
			logger.error("Failed storing Feature Bucket {} in Mongo collection {}.", featureBuckets, collectionName, e);
		}
	}

	private static String getCollectionName(String featureBucketConfName) {
		return COLLECTION_NAME_PREFIX + featureBucketConfName;
	}

	public static String getCollectionName(FeatureBucketConf featureBucketConf) {
		return getCollectionName(featureBucketConf.getName());
	}


	@Override
	public void setStoreManager(StoreManager storeManager) {
		this.storeManager = storeManager;
	}

	@Override
	public void remove(String collectionName, Instant until) {
		Query query = new Query()
				.addCriteria(where(FeatureBucket.START_TIME_FIELD).lt(until));
		mongoTemplate.remove(query, collectionName);
	}

	@Override
	public void remove(String collectionName, Instant start, Instant end){
		Query query = new Query()
				.addCriteria(where(FeatureBucket.START_TIME_FIELD).gte(start).lt(end));
		mongoTemplate.remove(query, collectionName);
	}
}
