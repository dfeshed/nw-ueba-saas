package fortscale.streaming.service.aggregation.bucket.strategy;

import java.util.List;
import fortscale.streaming.service.aggregation.FeatureBucketConf;
import net.minidev.json.JSONObject;

public interface FeatureBucketStrategy {
	public FeatureBucketStrategyData update(JSONObject event);
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec);


	/**
	 * Returns strategy data of the bucket tick which starts after the given startAfterEpochtimeInSeconds for the given context.
	 * @param bucketConf
	 * @param strategyId
	 * @param startAfterEpochtimeInSeconds
	 */
	public FeatureBucketStrategyData getNextBucketStrategyData(FeatureBucketConf bucketConf, String strategyId, long startAfterEpochtimeInSeconds);

	/**
	 * Register the listener to be called when a new strategy data (a.k.a 'bucket tick') is created for the given context and
	 * which its start time is after the given startAfterEpochtimeInSeconds.
	 * @param bucketConf
	 * @param strategyId
	 * @param listener
	 * @param startAfterEpochtimeInSeconds
	 */
	public void notifyWhenNextBucketEndTimeIsKnown(FeatureBucketConf bucketConf, String strategyId, NextBucketEndTimeListener listener, long startAfterEpochtimeInSeconds);
}
