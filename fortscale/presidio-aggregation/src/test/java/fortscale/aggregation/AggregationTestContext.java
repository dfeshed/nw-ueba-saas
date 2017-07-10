package fortscale.aggregation;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyMongoStore;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.RetentionStrategiesConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@Import({MongodbTestConfig.class, MongoDbUtilServiceConfig.class})
public class AggregationTestContext {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	@Bean
	public FeatureBucketStrategyMongoStore featureBucketStrategyStore() {
		return new FeatureBucketStrategyMongoStore();
	}

	@Bean
	public FeatureBucketStoreMongoImpl featureBucketStore() {
		return new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbUtilService, TimeUnit.DAYS.toSeconds(90));
	}

	@Bean
	public BucketConfigurationService bucketConfigurationService() {
		return new BucketConfigurationService();
	}

	@Bean
	public RetentionStrategiesConfService retentionStrategiesConfService() {
		return new RetentionStrategiesConfService();
	}

	@Bean
	public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {
		return new AggregatedFeatureEventsConfService();
	}

	@Bean
	public AggrFeatureFuncService aggrFeatureFuncService() {
		return new AggrFeatureFuncService();
	}
}
