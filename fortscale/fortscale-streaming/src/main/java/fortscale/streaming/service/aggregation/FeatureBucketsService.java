package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeatureBucketsService {
	private FixedDurationFeatureBucketStrategy fixedDurationStrategy;

	public FeatureBucketsService() {
		// Create mongo store for the feature buckets
		FeatureBucketsMongoStore mongoStore = new FeatureBucketsMongoStore();

		List<String> contextFieldNames = new ArrayList<>(2);
		contextFieldNames.add("normalized_username");
		contextFieldNames.add("normalized_src_machine");

		// Create a fixed duration feature bucket strategy of 1 hour
		fixedDurationStrategy = new FixedDurationFeatureBucketStrategy(contextFieldNames, 3600, mongoStore);
	}

	public void updateFeatureBuckets(JSONObject message, long timestamp) {
		fixedDurationStrategy.getFeatureBucket(message, timestamp);
	}
}
