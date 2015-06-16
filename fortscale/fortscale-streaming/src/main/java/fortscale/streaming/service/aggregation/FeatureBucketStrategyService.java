package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureBucketStrategyService {
	private Map<String, FeatureBucketStrategy> nameToStrategyMap;
	private FeatureBucketsStore featureBucketsStore;

	public FeatureBucketStrategyService(FeatureBucketsStore featureBucketsStore) {
		// Validate input
		Assert.notNull(featureBucketsStore);

		nameToStrategyMap = new HashMap<>();
		this.featureBucketsStore = featureBucketsStore;
	}

	public List<FeatureBucketWrapper> getFeatureBuckets(JSONObject message, FeatureBucketConf conf) {
		FeatureBucketStrategy strategy = getFeatureBucketStrategy(conf.getStrategyName());
		return strategy.getFeatureBuckets(message, conf, featureBucketsStore);
	}

	private FeatureBucketStrategy getFeatureBucketStrategy(String strategyName) {
		if (!nameToStrategyMap.containsKey(strategyName)) {
			nameToStrategyMap.put(strategyName, createFeatureBucketStrategy(strategyName));
		}

		return nameToStrategyMap.get(strategyName);
	}

	private FeatureBucketStrategy createFeatureBucketStrategy(String strategyName) {
		// TODO implement (should be static maybe?)
		return null;
	}
}
