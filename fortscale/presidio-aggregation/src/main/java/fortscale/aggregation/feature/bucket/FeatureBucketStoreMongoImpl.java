package fortscale.aggregation.feature.bucket;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	/**
	 * C'tor.
	 * @param mongoTemplate             the {@link MongoTemplate}
	 * @param mongoDbBulkOpUtil
	 */
	public FeatureBucketStoreMongoImpl(
			MongoTemplate mongoTemplate, MongoDbBulkOpUtil mongoDbBulkOpUtil) {

		this.mongoTemplate = mongoTemplate;
		this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
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
	 * @see FeatureBucketStore#storeFeatureBucket(FeatureBucketConf, List, StoreManagerMetadataProperties)
	 */
	@Override
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, List<FeatureBucket> featureBuckets, StoreManagerMetadataProperties storeManagerMetadataProperties) {
		String collectionName = getCollectionName(featureBucketConf);

		try {
			mongoDbBulkOpUtil.insertUnordered(featureBuckets,collectionName);
			storeManager.registerWithTtl(getStoreName(), collectionName, storeManagerMetadataProperties.getProperties());
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
