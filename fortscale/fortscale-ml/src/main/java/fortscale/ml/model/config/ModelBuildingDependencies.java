package fortscale.ml.model.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.util.MongoDbUtilService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelBuildingDependencies {
	@Bean
	public BucketConfigurationService bucketConfigurationService() {
		return new BucketConfigurationService();
	}

	@Bean
	public FeatureBucketsReaderService featureBucketsReaderService() {
		return new FeatureBucketsReaderService();
	}

	@Bean
	public FeatureBucketsMongoStore featureBucketsMongoStore() {
		return new FeatureBucketsMongoStore();
	}

	@Bean
	public MongoDbUtilService mongoDbUtilService() {
		return new MongoDbUtilService();
	}
}
