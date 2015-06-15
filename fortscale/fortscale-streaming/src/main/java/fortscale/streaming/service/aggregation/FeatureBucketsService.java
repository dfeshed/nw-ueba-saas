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

	// Currently hold a single strategy
	private FixedDurationFeatureBucketStrategy fixedDurationStrategy;

	public FeatureBucketsService() {
		List<String> contextFieldNames = new ArrayList<>();
		contextFieldNames.add("normalized_username");
		contextFieldNames.add("normalized_dst_machine");

		// Create a fixed duration feature bucket strategy of 1 hour with the above contexts
		fixedDurationStrategy = new FixedDurationFeatureBucketStrategy(contextFieldNames, 3600, featureBucketsMongoStore);
	}

	public void updateFeatureBuckets(JSONObject message, long timestamp) {
		FeatureBucket featureBucket = fixedDurationStrategy.getFeatureBucket(message, timestamp);

		if (featureBucket == null) {
			featureBucket = new FeatureBucket();
			// Currently add to the bucket a single feature that simply counts the number of events in it
			featureBucket.addFeature(EventCounterFeatureExtractor.NAME, EventCounterFeatureExtractor.createFeature());
		}

		for (Map.Entry<String, Object> entry : featureBucket.getFeatures().entrySet()) {
			// Update the only feature existing in the bucket (the event counter)
			if (entry.getKey().equals(EventCounterFeatureExtractor.NAME)) {
				EventCounterFeatureExtractor.updateFeature(entry.getValue(), message);
			}
		}

		fixedDurationStrategy.saveFeatureBucket(message, timestamp, featureBucket);
	}
}
