package fortscale.aggregation.feature.bucket.repository;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class FeatureBucketMetadataRepositoryImpl implements FeatureBucketMetadataRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<FeatureBucketMetadata> updateFeatureBucketsEndTime(String featureBucketConfName, String strategyId, long newCloseTime) {
		Update update = new Update();
		update.set(FeatureBucketMetadata.END_TIME_FIELD, newCloseTime);
		Query query = new Query(Criteria.where(FeatureBucketMetadata.STRATEGY_ID_FIELD).is(strategyId).and(FeatureBucketMetadata.FEATURE_BUCKET_CONF_NAME_FIELD).is(featureBucketConfName));
		WriteResult writeResult = mongoTemplate.updateMulti(query, update, FeatureBucketMetadata.class, FeatureBucketMetadata.COLLECTION_NAME);
		if (writeResult.getN() > 0) {
			return mongoTemplate.find(query, FeatureBucketMetadata.class, FeatureBucketMetadata.COLLECTION_NAME);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<FeatureBucketMetadata> findByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime) {
		Query query = new Query(where(FeatureBucketMetadata.END_TIME_FIELD).lt(endTime).and(FeatureBucketMetadata.SYNC_TIME_FIELD).lt(syncTime));
		return mongoTemplate.find(query, FeatureBucketMetadata.class);
	}

	@Override
	public void deleteByEndTimeLessThanAndSyncTimeLessThan(long endTime, long syncTime) {
		Query query = new Query(where(FeatureBucketMetadata.END_TIME_FIELD).lt(endTime).and(FeatureBucketMetadata.SYNC_TIME_FIELD).lt(syncTime));
		mongoTemplate.remove(query, FeatureBucketMetadata.class);
	}

	@Override
	public void updateByIsSyncedFalseAndEndTimeLessThanWithSyncedTrueAndSyncTime(long endTime, long syncTime) {
		Query query = new Query(Criteria.where(FeatureBucketMetadata.IS_SYNCED_FIELD).is(false).and(FeatureBucketMetadata.END_TIME_FIELD).lt(endTime));
		Update update = new Update();
		update.set(FeatureBucketMetadata.IS_SYNCED_FIELD, true);
		update.set(FeatureBucketMetadata.SYNC_TIME_FIELD, syncTime);
		mongoTemplate.updateMulti(query, update, FeatureBucketMetadata.class, FeatureBucketMetadata.COLLECTION_NAME);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findDistinctFeatureBucketConfNamesByIsSyncedFalseAndEndTimeLessThan(long epochtime) {
		Query query = new Query();
		query.addCriteria(Criteria.where(FeatureBucketMetadata.IS_SYNCED_FIELD).is(false));
		query.addCriteria(Criteria.where(FeatureBucketMetadata.END_TIME_FIELD).lt(epochtime));
		return mongoTemplate.getCollection(FeatureBucketMetadata.COLLECTION_NAME).distinct(
				FeatureBucketMetadata.FEATURE_BUCKET_CONF_NAME_FIELD, query.getQueryObject());
	}
}
