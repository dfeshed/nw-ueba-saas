package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import java.util.List;

public class FeatureBucketsService {
	private FeatureBucketsStore featureBucketsStore;
	private FeatureBucketStrategyService featureBucketStrategyService;

	public FeatureBucketsService() {
		featureBucketsStore = new FeatureBucketsMongoStore();
		featureBucketStrategyService = new FeatureBucketStrategyService(featureBucketsStore);
	}

	public void updateFeatureBuckets(JSONObject message, List<FeatureBucketConf> featureBucketConfs) {
		for (FeatureBucketConf conf : featureBucketConfs) {
			List<FeatureBucketWrapper> featureBuckets = featureBucketStrategyService.getFeatureBuckets(message, conf);

			for (FeatureBucketWrapper wrapper : featureBuckets) {
				FeatureBucket featureBucket = wrapper.getFeatureBucket();
				// TODO extract features, etc...
			}
		}
	}
}
