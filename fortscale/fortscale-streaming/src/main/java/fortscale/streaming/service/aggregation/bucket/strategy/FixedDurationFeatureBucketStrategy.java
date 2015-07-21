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
		this.strategyName = strategyName;
	}

	@Override
	public FeatureBucketStrategyData update(JSONObject event) {
		return null;
	}

	private List<FeatureBucketStrategyData> getFeatureBucketStrategyData(long epochtimeInSec){
		long startTime = (epochtimeInSec / durationInSeconds) * durationInSeconds;
		FeatureBucketStrategyData featureBucketStrategyData = new FeatureBucketStrategyData(strategyName, strategyName, startTime, startTime + durationInSeconds);
		List<FeatureBucketStrategyData> ret = new ArrayList<FeatureBucketStrategyData>();
		ret.add(featureBucketStrategyData);
		return ret;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec){
		return getFeatureBucketStrategyData(epochtimeInSec);
	}

	@Override
	public FeatureBucketStrategyData getNextBucketStrategyData(FeatureBucketConf bucketConf, String strategyId, long startAfterEpochtimeInSeconds) {
		List<FeatureBucketStrategyData> strategyDatas = getFeatureBucketStrategyData(startAfterEpochtimeInSeconds +1);
		return strategyDatas.get(0);
	}

	/**
	 * Register the listener to be called when a new strategy data (a.k.a 'bucket tick') is created for the given context and
	 * which its start time is after the given startAfterEpochtimeInSeconds.
	 * @param bucketConf
	 * @param strategyId
	 * @param listener
	 * @param startAfterEpochtimeInSeconds
	 */
	@Override
	public void notifyWhenNextBucketEndTimeIsKnown(FeatureBucketConf bucketConf, String strategyId, NextBucketEndTimeListener listener,  long startAfterEpochtimeInSeconds) {
		// This method shouldn't be used because getNextBucketStrategyData will always return the next bucket data,
		// but to be on the safe side implementation is provided here.
		listener.nextBucketEndTimeUpdate(getNextBucketStrategyData(bucketConf, strategyId, startAfterEpochtimeInSeconds));
	}


}
