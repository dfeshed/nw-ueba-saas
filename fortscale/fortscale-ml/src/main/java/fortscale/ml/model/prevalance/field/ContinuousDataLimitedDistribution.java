package fortscale.ml.model.prevalance.field;

import org.apache.samza.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ContinuousDataLimitedDistribution extends ContinuousDataDistribution {
	private static final int DEFAULT_MAX_NUM_OF_DATA_BUCKETS = 100;

	private int maxNumOfDataBuckets;

	@Override
	public void init(String prefix, String fieldName, Config config) {
		super.init(prefix, fieldName, config);

		String maxBucketsFieldname = String.format("%s.%s.continuous.data.distribution.max.buckets", prefix, fieldName);
		maxNumOfDataBuckets = config.getInt(maxBucketsFieldname, DEFAULT_MAX_NUM_OF_DATA_BUCKETS);
	}

	@Override
	protected void updateDistribution() {
		// Double the size of the buckets until the number of buckets does not exceed the maximum
		while (distribution.size() > maxNumOfDataBuckets) {
			bucketSize *= 2;
			Map<Double, Long> newDistribution = new HashMap<>();

			// Move the values to the new distribution, while taking into account the new bucket size
			for (Map.Entry<Double, Long> entry : distribution.entrySet()) {
				Double value = roundValue(entry.getKey());
				Long oldCount = newDistribution.get(value);
				Long newCount = oldCount == null ? 1 : oldCount + 1;
				newDistribution.put(value, newCount);
			}

			distribution = newDistribution;
		}
	}
}
