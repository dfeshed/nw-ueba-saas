package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static fortscale.utils.ConversionUtils.convertToString;

public class FixedDurationFeatureBucketStrategy {
	private List<String> contextFieldNames;
	private long durationInSeconds;
	private String strategyId;
	private FeatureBucketsMongoStore mongoStore;

	public FixedDurationFeatureBucketStrategy(List<String> contextFieldNames, long durationInSeconds, FeatureBucketsMongoStore mongoStore) {
		// Validate context field names
		Assert.notNull(contextFieldNames, "Must accept a list of context field names");
		Assert.notEmpty(contextFieldNames, "List of context field names cannot be empty");
		for (String contextFieldName : contextFieldNames) {
			Assert.isTrue(StringUtils.isNotBlank(contextFieldName), "Invalid blank context field name");
		}
		this.contextFieldNames = contextFieldNames;

		// Validate the fixed duration
		Assert.isTrue(durationInSeconds > 0, "Fixed duration must be positive");
		this.durationInSeconds = durationInSeconds;

		// Set this strategy's ID
		strategyId = String.format("%s%d", FixedDurationFeatureBucketStrategy.class.getName(), durationInSeconds);

		// Validate the feature buckets mongo store
		Assert.notNull(mongoStore, "Must accept a mongo store of feature buckets");
		this.mongoStore = mongoStore;
	}

	public FeatureBucket getFeatureBucket(JSONObject message, long timestamp) {
		Map<String, String> contextFieldNameToValueMap = mapContextFieldNamesToValues(message);
		long startTime = (timestamp / durationInSeconds) * durationInSeconds;
		return mongoStore.getFeatureBucket(contextFieldNameToValueMap, strategyId, startTime);
	}

	public void saveFeatureBucket(JSONObject message, long timestamp, FeatureBucket featureBucket) {
		Map<String, String> contextFieldNameToValueMap = mapContextFieldNamesToValues(message);
		long startTime = (timestamp / durationInSeconds) * durationInSeconds;
		mongoStore.saveFeatureBucket(contextFieldNameToValueMap, strategyId, startTime, featureBucket);
	}

	private Map<String, String> mapContextFieldNamesToValues(JSONObject message) {
		Map<String, String> contextFieldNameToValueMap = new HashMap<>();

		for (String contextFieldName : contextFieldNames) {
			if (message.containsKey(contextFieldName)) {
				String contextValue = convertToString(message.get(contextFieldName));

				// Add only valid context values
				if (StringUtils.isBlank(contextValue)) {
					contextFieldNameToValueMap.put(contextFieldName, contextValue);
				}
			}
		}

		return contextFieldNameToValueMap;
	}
}
