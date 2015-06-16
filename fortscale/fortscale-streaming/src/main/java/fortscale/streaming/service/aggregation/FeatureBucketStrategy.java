package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import java.util.List;

public interface FeatureBucketStrategy {
	public List<FeatureBucketWrapper> getFeatureBuckets(JSONObject message, FeatureBucketConf conf, FeatureBucketsStore store);
}
