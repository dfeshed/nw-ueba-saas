package fortscale.streaming.service.aggregation.feature.bucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.aggregation.feature.extraction.FeatureExtractService;
import fortscale.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.streaming.ExtendedSamzaTaskContext;

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
