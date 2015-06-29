package fortscale.streaming.service.aggregation.samza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.aggregation.feature.extraction.FeatureExtractService;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.streaming.service.aggregation.FeatureBucketsService;
import fortscale.streaming.service.aggregation.FeatureBucketsStore;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;

@Configurable(preConstruction=true)
public class FeatureBucketsServiceSamza extends FeatureBucketsService {

	private FeatureBucketsStore featureBucketsStore;
	private FeatureBucketStrategyService featureBucketStrategyService;
	
	@Autowired
	private FeatureExtractService featureExtractService;
	
	@Autowired
	private IAggrFeatureFunctionsService aggrFeatureFunctionsService;
	
	
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


	@Override
	protected FeatureExtractService getFeatureExtractService() {
		return featureExtractService;
	}


	@Override
	protected IAggrFeatureFunctionsService getAggrFeatureFunctionsService() {
		return aggrFeatureFunctionsService;
	}
	
	
}
