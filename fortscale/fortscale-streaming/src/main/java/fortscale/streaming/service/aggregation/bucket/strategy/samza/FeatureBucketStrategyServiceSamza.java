package fortscale.streaming.service.aggregation.bucket.strategy.samza;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.FeatureBucketsStore;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategiesFactory;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyService;


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
