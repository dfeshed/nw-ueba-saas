package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToString;

public class FixedDurationFeatureBucketStrategy {
	private static final Logger logger = LoggerFactory.getLogger(FixedDurationFeatureBucketStrategy.class);

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
		// Map each context field to its value in the message
		Map<String, String> contextFieldNameToValueMap = new HashMap<>();

		for (String contextFieldName : contextFieldNames) {
			if (!message.containsKey(contextFieldName)) {
				logger.warn(String.format("Event message does not contain the field %s", contextFieldName));
				return null;
			}

			String contextValue = convertToString(message.get(contextFieldName));
			if (StringUtils.isBlank(contextValue)) {
				logger.warn(String.format("Value of field %s in the event message is blank", contextFieldName));
				return null;
			}

			contextFieldNameToValueMap.put(contextFieldName, contextValue);
		}

		// Calculate the start time of the feature bucket to which the message belongs
		long startTime = (timestamp / durationInSeconds) * durationInSeconds;

		return mongoStore.getFeatureBucket(contextFieldNameToValueMap, strategyId, startTime);
	}
}
