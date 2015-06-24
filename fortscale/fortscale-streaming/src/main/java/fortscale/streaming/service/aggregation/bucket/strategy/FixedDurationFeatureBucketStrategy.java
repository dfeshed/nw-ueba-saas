package fortscale.streaming.service.aggregation.bucket.strategy;

import java.util.List;

import net.minidev.json.JSONObject;

import org.springframework.util.Assert;

import fortscale.streaming.service.aggregation.FeatureBucketConf;

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
	public FeatureBucketStrategyData update(JSONObject event) {
		return null;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(JSONObject event, FeatureBucketConf featureBucketConf) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
