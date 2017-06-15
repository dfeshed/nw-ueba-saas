package fortscale.aggregation.domain.feature.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class FeatureBucketAggrMetadataRepositoryImpl implements FeatureBucketAggrMetadataRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void updateFeatureBucketsEndTime(String bucketConfName, String bucketId, long newCloseTime) {
		Update update = new Update();
		update.set(FeatureBucketAggrMetadata.END_TIME_FIELD, newCloseTime);
		Query query = new Query(Criteria.where(FeatureBucketAggrMetadata.BUCKET_ID_FIELD).is(bucketId).and(FeatureBucketAggrMetadata.FEATURE_BUCKET_CONF_NAME_FIELD).is(bucketConfName));
		
		mongoTemplate.updateMulti(query, update, FeatureBucketAggrMetadata.class, FeatureBucketAggrMetadata.COLLECTION_NAME);
	}
	
	
	public List<FeatureBucketAggrMetadata> findByEndTimeLessThanSortedByEndTimeAsc(Long endTime){
		Query query = new Query(Criteria.where(FeatureBucketAggrMetadata.END_TIME_FIELD).lt(endTime));
		query.with(new Sort(Direction.ASC, FeatureBucketAggrMetadata.END_TIME_FIELD));
		return mongoTemplate.find(query, FeatureBucketAggrMetadata.class);
	}


	@Override
	public void deleteByEndTimeLessThan(Long endTime) {
		Query query = new Query(Criteria.where(FeatureBucketAggrMetadata.END_TIME_FIELD).lt(endTime));
		mongoTemplate.remove(query,  FeatureBucketAggrMetadata.class);
	}

}
