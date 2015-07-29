package fortscale.aggregation.feature.bucket;

import com.mongodb.WriteResult;
import fortscale.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("featureBucketsStore")
public class FeatureBucketsMongoStore implements FeatureBucketsStore {
	private static final String COLLECTION_NAME_PREFIX = "aggr_";
	
	
	@Autowired
	private MongoTemplate mongoTemplate;

	
	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoTemplate.collectionExists(collectionName)) {
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
	public List<FeatureBucket> getFeatureBuckets(FeatureBucketConf featureBucketConf, String entityType, String entityName, String feature, Long startTime, Long endTime) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoTemplate.collectionExists(collectionName)) {
			Criteria bucketStartTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
			Criteria bucketEndTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
			Criteria contextCriteria = createContextCriteria(entityType, entityName);

			Query query = new Query(bucketStartTimeCriteria.andOperator(bucketEndTimeCriteria,contextCriteria));

			return mongoTemplate.find(query, FeatureBucket.class, collectionName);
		}
		else {
			throw new RuntimeException("Could not fetch feature buckets from collection " + collectionName);
		}
	}

	private Criteria createContextCriteria(String entityType, String entityName) {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put(entityType, entityName);
		return Criteria.where(FeatureBucket.CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD).in(contextMap); // TODO check for multiple context
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoTemplate.collectionExists(collectionName)) {
			Query query = new Query(Criteria.where(FeatureBucket.BUCKET_ID_FIELD).is(bucketId));
			
			return mongoTemplate.findOne(query, FeatureBucket.class, collectionName);
		}
		return null;
	}
	
	public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket){
		String collectionName = getCollectionName(featureBucketConf);
		if (!isCollectionExist(collectionName)) {
			mongoTemplate.createCollection(collectionName);
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(FeatureBucket.BUCKET_ID_FIELD,Direction.DESC).unique(Duplicates.DROP));
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(FeatureBucket.STRATEGY_ID_FIELD,Direction.DESC));
		}
		mongoTemplate.save(featureBucket, collectionName);
	}
	
	private boolean isCollectionExist(String collectionName){
		return mongoTemplate.collectionExists(collectionName);
	}
	
	private String getCollectionName(FeatureBucketConf featureBucketConf){
		return String.format("%s%s", COLLECTION_NAME_PREFIX, featureBucketConf.getName());
	}
}
