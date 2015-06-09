package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class FeatureBucketsService {
	@Autowired
	private FeatureBucketsMongoStore featureBucketsMongoStore;

	private FixedDurationFeatureBucketStrategy fixedDurationStrategy;

	public FeatureBucketsService() {
		List<String> contextFieldNames = new ArrayList<>(2);
		contextFieldNames.add("normalized_username");
		contextFieldNames.add("normalized_src_machine");

		// Create a fixed duration feature bucket strategy of 1 hour
		fixedDurationStrategy = new FixedDurationFeatureBucketStrategy(contextFieldNames, 3600, featureBucketsMongoStore);
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
