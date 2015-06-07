package fortscale.streaming.service.aggregation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

public class FeatureBucketsMongoStore {
	@Autowired
	private MongoTemplate mongoTemplate;

	public FeatureBucket getFeatureBucket(Map<String, String> contextFieldNameToValueMap, String strategyId, long startTime) {
		String collectionName = getCollectionName(contextFieldNameToValueMap.keySet());
		String userName = contextFieldNameToValueMap.get("normalized_username");
		String machineName = contextFieldNameToValueMap.get("normalized_src_machine");

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

	private static String getCollectionName(Set<String> contextFieldNames) {
		List<String> sorted = new ArrayList<>(contextFieldNames);
		Collections.sort(sorted);
		return StringUtils.join(sorted, '.');
	}
}
