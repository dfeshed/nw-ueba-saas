package fortscale.streaming.service.aggregation.bucket.strategy;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.springframework.util.Assert;

import fortscale.streaming.service.aggregation.FeatureBucketConf;

public class FixedDurationFeatureBucketStrategy implements FeatureBucketStrategy {
	private long durationInSeconds;
	private String strategyName;

	public FixedDurationFeatureBucketStrategy(String strategyName, long durationInSeconds) {
		// Validate the fixed duration
		Assert.isTrue(durationInSeconds > 0, "Fixed duration must be positive");
		this.durationInSeconds = durationInSeconds;

	}

	@Override
	public FeatureBucketStrategyData update(JSONObject event) {
		return null;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec){
		long startTime = (epochtimeInSec / durationInSeconds) * durationInSeconds;
		FeatureBucketStrategyData featureBucketStrategyData = new FeatureBucketStrategyData(strategyName, strategyName, startTime, startTime + durationInSeconds);
		List<FeatureBucketStrategyData> ret = new ArrayList<FeatureBucketStrategyData>();
		ret.add(featureBucketStrategyData);
		return ret;
	}

	
}
