package presidio.ade.modeling.config;

import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({
		ModelingServiceFeatureBucketConfServiceConfig.class,
		ModelingServiceFeatureAggregationEventConfServiceConfig.class,
		ModelingServiceEntityEventConfServiceConfig.class,
		AggregationEventsAccumulationDataReaderConfig.class
})
public class ModelingServiceDependencies {
	@Value("${presidio.ade.modeling.feature.buckets.default.expire.after.seconds}")
	private long featureBucketsDefaultExpireAfterSeconds;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	/*******************************
	 * Feature bucket related beans.
	 *******************************/

	@Bean
	public FeatureBucketReader featureBucketReader() {
		return new FeatureBucketStoreMongoImpl(
				mongoTemplate,
				mongoDbUtilService,
				featureBucketsDefaultExpireAfterSeconds);
	}

	/*****************************
	 * Smart record related beans.
	 *****************************/
}
