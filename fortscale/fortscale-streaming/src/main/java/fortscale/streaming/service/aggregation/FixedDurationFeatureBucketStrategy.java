package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;
import java.util.List;

public class FixedDurationFeatureBucketStrategy implements FeatureBucketStrategy {
	private static final String STRATEGY_ID_PREFIX = "FIXED_DURATION";

	private long durationInSeconds;
	private String strategyId;

	public FixedDurationFeatureBucketStrategy(long durationInSeconds) {
		// Validate the fixed duration
		Assert.isTrue(durationInSeconds > 0, "Fixed duration must be positive");
		this.durationInSeconds = durationInSeconds;

		strategyId = String.format("%s_%d", STRATEGY_ID_PREFIX, durationInSeconds);
	}

	@Override
	public List<FeatureBucketWrapper> getFeatureBuckets(JSONObject message, FeatureBucketConf conf, FeatureBucketsStore store) {
		// TODO implement
		return null;
	}
}
