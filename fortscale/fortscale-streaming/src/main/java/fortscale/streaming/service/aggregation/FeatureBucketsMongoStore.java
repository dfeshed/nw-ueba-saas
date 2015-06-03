package fortscale.streaming.service.aggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

public class FeatureBucketsMongoStore {
	@Autowired
	private MongoTemplate mongoTemplate;

	public FeatureBucket getFeatureBucket(Map<String, String> contextFieldNameToValueMap, long startTime, String strategyName) {
		return null;
	}
}
