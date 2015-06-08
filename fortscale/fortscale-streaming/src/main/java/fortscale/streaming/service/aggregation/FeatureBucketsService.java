package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		FeatureBucket featureBucket = fixedDurationStrategy.getFeatureBucket(message, timestamp);

		if (featureBucket == null) {
			featureBucket = new FeatureBucket();
			featureBucket.addFeature("event_counter", EventCounterFeatureExtractor.createFeature());
		}

		for (Map.Entry<String, Object> entry : featureBucket.getFeatures().entrySet()) {
			if (entry.getKey().equals("event_counter")) {
				EventCounterFeatureExtractor.updateFeature(entry.getValue(), message);
			}
		}

		fixedDurationStrategy.saveFeatureBucket(message, timestamp, featureBucket);
	}
}
