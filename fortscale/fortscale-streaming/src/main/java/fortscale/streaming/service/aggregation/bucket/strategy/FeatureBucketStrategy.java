package fortscale.streaming.service.aggregation.bucket.strategy;

import java.util.List;
import java.util.Map;

import fortscale.streaming.service.aggregation.FeatureBucketConf;
import net.minidev.json.JSONObject;

public interface FeatureBucketStrategy {
	public FeatureBucketStrategyData update(JSONObject event);
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec);

	public FeatureBucketStrategyData getNextBucketStrategyData(FeatureBucketConf bucketConf, Map<String, String> context);
	public void notifyWhenNextBucketEndTimeIsKnown(FeatureBucketConf bucketConf, Map<String, String> context, NextBucketEndTimeListener listener);
}
