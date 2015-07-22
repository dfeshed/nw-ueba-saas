package fortscale.streaming.service.aggregation.feature.bucket.strategy;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategiesFactory;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyService;
import fortscale.streaming.ExtendedSamzaTaskContext;


@Configurable(preConstruction=true)
public class FeatureBucketStrategyServiceSamza extends FeatureBucketStrategyService {
	
	private FeatureBucketStrategiesFactory featureBucketStrategiesFactory;
	
	private FeatureBucketsStore featureBucketsStore;
	
	public FeatureBucketStrategyServiceSamza(@NotNull ExtendedSamzaTaskContext context, @NotNull FeatureBucketsStore featureBucketsStore) {
		this.featureBucketStrategiesFactory = new FeatureBucketStrategiesFactorySamza(context);
		this.featureBucketsStore = featureBucketsStore;
	}

	@Override
	public FeatureBucketStrategiesFactory getFeatureBucketStrategiesFactory() {
		return featureBucketStrategiesFactory;
	}

	@Override
	public FeatureBucketsStore getFeatureBucketsStore() {
		return featureBucketsStore;
	}

}
