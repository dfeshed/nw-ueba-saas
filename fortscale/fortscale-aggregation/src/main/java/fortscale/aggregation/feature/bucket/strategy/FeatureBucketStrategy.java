package fortscale.aggregation.feature.bucket.strategy;

import java.util.List;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.common.event.Event;

public interface FeatureBucketStrategy {
	public FeatureBucketStrategyData update(Event event);
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, Event event, long epochtimeInSec);


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

	/**
	 *
	 * @param strategyId
	 * @return the strategy context of the given startegyId
	 * @throws IllegalArgumentException
	 */
	public String getStrategyContextIdFromStrategyId(String strategyId) throws IllegalArgumentException;
}
