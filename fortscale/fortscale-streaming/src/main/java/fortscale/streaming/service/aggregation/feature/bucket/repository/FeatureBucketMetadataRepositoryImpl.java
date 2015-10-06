package fortscale.streaming.service.aggregation.feature.bucket.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class FeatureBucketMetadataRepositoryImpl implements FeatureBucketMetadataRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void updateFeatureBucketsEndTime(String bucketName, String strategyId, long newCloseTime){
		Update update = new Update();
		update.set(FeatureBucketMetadata.END_TIME_FIELD, newCloseTime);
		Query query = new Query(Criteria.where(FeatureBucketMetadata.STRATEGY_ID_FIELD).is(strategyId).and(FeatureBucketMetadata.FEATURE_BUCKET_CONF_NAME_FIELD).is(bucketName));
		
		mongoTemplate.updateMulti(query, update, FeatureBucketMetadata.class, FeatureBucketMetadata.COLLECTION_NAME);
	}
	
	@Override
	public List<FeatureBucketMetadata> findByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime){
		Query query = new Query(where(FeatureBucketMetadata.END_TIME_FIELD).lt(endTime).and(FeatureBucketMetadata.SYNC_TIME_FIELD).lt(syncTime));
		return mongoTemplate.find(query, FeatureBucketMetadata.class);
	}
}
