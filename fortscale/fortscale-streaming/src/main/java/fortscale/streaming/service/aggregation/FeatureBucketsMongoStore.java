package fortscale.streaming.service.aggregation;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;


@Component
public class FeatureBucketsMongoStore implements FeatureBucketsStore {
	@Autowired
	private MongoTemplate mongoTemplate;

	
	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId, long newCloseTime) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoTemplate.collectionExists(collectionName)) {
			Query query = new Query(where(FeatureBucket.STRATEGY_ID_FIELD).is(strategyId));

			return mongoTemplate.find(query, FeatureBucket.class, collectionName);
		}

		return Collections.emptyList();
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId) {
		String collectionName = getCollectionName(featureBucketConf);

		if (mongoTemplate.collectionExists(collectionName)) {
			Query query = new Query(where(FeatureBucket.BUCKET_ID_FIELD).is(bucketId));
			
			return mongoTemplate.findOne(query, FeatureBucket.class, collectionName);
		}
		return null;
	}
	
	private String getCollectionName(FeatureBucketConf featureBucketConf){
		return featureBucketConf.getName();
	}
}
