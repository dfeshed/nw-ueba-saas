package fortscale.streaming.service.aggregation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("featureBucketsMongoStore")
public class FeatureBucketsMongoStore {
	private static final String NORMALIZED_USERNAME_FIELD = "normalized_username";
	private static final String NORMALIZED_DST_MACHINE_FIELD = "normalized_dst_machine";

	@Autowired
	private MongoTemplate mongoTemplate;

	public FeatureBucket getFeatureBucket(Map<String, String> contextFieldNameToValueMap, String strategyId, long startTime) {
		String collectionName = getCollectionName(contextFieldNameToValueMap.keySet());
		String userName = contextFieldNameToValueMap.get(NORMALIZED_USERNAME_FIELD);
		String machineName = contextFieldNameToValueMap.get(NORMALIZED_DST_MACHINE_FIELD);

		if (mongoTemplate.collectionExists(collectionName)) {
			Query query = new Query();
			query.addCriteria(Criteria.where(FeatureBucket.STRATEGY_ID_FIELD).is(strategyId));
			query.addCriteria(Criteria.where(FeatureBucket.USER_NAME_FIELD).is(userName));
			query.addCriteria(Criteria.where(FeatureBucket.MACHINE_NAME_FIELD).is(machineName));
			query.addCriteria(Criteria.where(FeatureBucket.START_TIME_FIELD).is(startTime));

			return mongoTemplate.findOne(query, FeatureBucket.class, collectionName);
		}

		return null;
	}

	public void saveFeatureBucket(Map<String, String> contextFieldNameToValueMap, String strategyId, long startTime, FeatureBucket featureBucket) {
		String collectionName = getCollectionName(contextFieldNameToValueMap.keySet());
		FeatureBucket inMongoStore = getFeatureBucket(contextFieldNameToValueMap, strategyId, startTime);

		if (inMongoStore == null) {
			if (!mongoTemplate.collectionExists(collectionName)) {
				mongoTemplate.createCollection(collectionName);
			}

			featureBucket.setStrategyId(strategyId);
			featureBucket.setUserName(contextFieldNameToValueMap.get(NORMALIZED_USERNAME_FIELD));
			featureBucket.setMachineName(contextFieldNameToValueMap.get(NORMALIZED_DST_MACHINE_FIELD));
			featureBucket.setStartTime(startTime);
			featureBucket.setEndTime(0);

			// Save new feature bucket in mongo
			mongoTemplate.save(featureBucket, collectionName);
		} else {
			inMongoStore.setFeatures(featureBucket.getFeatures());

			// Update existing feature bucket in mongo
			mongoTemplate.save(inMongoStore, collectionName);
		}
	}

	private static String getCollectionName(Set<String> contextFieldNames) {
		List<String> sorted = new ArrayList<>(contextFieldNames);
		Collections.sort(sorted);
		return String.format("aggregation.%s", StringUtils.join(sorted, '.'));
	}
}
