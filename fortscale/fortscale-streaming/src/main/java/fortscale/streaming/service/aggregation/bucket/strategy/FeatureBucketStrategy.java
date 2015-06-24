package fortscale.streaming.service.aggregation.bucket.strategy;

import java.util.List;

import fortscale.streaming.service.aggregation.FeatureBucketConf;
import net.minidev.json.JSONObject;

public interface FeatureBucketStrategy {
	public FeatureBucketStrategyData update(JSONObject event);
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(JSONObject event, FeatureBucketConf featureBucketConf);
}
