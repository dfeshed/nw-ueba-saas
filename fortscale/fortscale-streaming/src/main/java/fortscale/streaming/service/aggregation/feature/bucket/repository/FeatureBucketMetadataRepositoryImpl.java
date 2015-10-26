package fortscale.streaming.service.aggregation.feature.bucket.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

public class FeatureBucketMetadataRepositoryImpl implements FeatureBucketMetadataRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<FeatureBucketMetadata> updateFeatureBucketsEndTime(String featureBucketConfName, String strategyId, long newCloseTime){
		Update update = new Update();
		update.set(FeatureBucketMetadata.END_TIME_FIELD, newCloseTime);
		Query query = new Query(Criteria.where(FeatureBucketMetadata.STRATEGY_ID_FIELD).is(strategyId).and(FeatureBucketMetadata.FEATURE_BUCKET_CONF_NAME_FIELD).is(featureBucketConfName));
		
		
		
		WriteResult writeResult = mongoTemplate.updateMulti(query, update, FeatureBucketMetadata.class, FeatureBucketMetadata.COLLECTION_NAME);
		if(writeResult.getN()>0){
			return mongoTemplate.find(query, FeatureBucketMetadata.class, FeatureBucketMetadata.COLLECTION_NAME);
		} else{
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<FeatureBucketMetadata> findByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime){
		Query query = new Query(where(FeatureBucketMetadata.END_TIME_FIELD).lt(endTime).and(FeatureBucketMetadata.SYNC_TIME_FIELD).lt(syncTime));
		return mongoTemplate.find(query, FeatureBucketMetadata.class);
	}

	@Override
	public void deleteByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime) {
		Query query = new Query(where(FeatureBucketMetadata.END_TIME_FIELD).lt(endTime).and(FeatureBucketMetadata.SYNC_TIME_FIELD).lt(syncTime));
		mongoTemplate.remove(query, FeatureBucketMetadata.class);
	}
}
