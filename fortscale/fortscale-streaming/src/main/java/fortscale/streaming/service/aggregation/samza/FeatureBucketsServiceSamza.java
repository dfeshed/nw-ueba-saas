package fortscale.streaming.service.aggregation.samza;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.FeatureBucketsService;
import fortscale.streaming.service.aggregation.FeatureBucketsStore;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;

public class FeatureBucketsServiceSamza extends FeatureBucketsService {

	private FeatureBucketsStore featureBucketsStore;
	private FeatureBucketStrategyService featureBucketStrategyService;
	
	
	public FeatureBucketsServiceSamza(ExtendedSamzaTaskContext context, FeatureBucketsStore featureBucketsStore, FeatureBucketStrategyService featureBucketStrategyService){
		this.featureBucketsStore = featureBucketsStore;
		this.featureBucketStrategyService = featureBucketStrategyService;
	}
	
	
	@Override
	public FeatureBucketsStore getFeatureBucketsStore() {
		return featureBucketsStore;
	}
	@Override
	public FeatureBucketStrategyService getFeatureBucketStrategyService() {
		return featureBucketStrategyService;
	}
	
	
}
