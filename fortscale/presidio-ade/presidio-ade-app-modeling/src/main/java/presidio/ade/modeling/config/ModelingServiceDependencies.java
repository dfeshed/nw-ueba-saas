package presidio.ade.modeling.config;

import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStoreConfig;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({
		ModelingServiceFeatureBucketConfServiceConfig.class,
		ModelingServiceFeatureAggregationEventConfServiceConfig.class,
		ModelingServiceSmartRecordConfServiceConfig.class,
		AggregationEventsAccumulationDataReaderConfig.class,
		SmartAccumulationDataStoreConfig.class,
})
public class ModelingServiceDependencies {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbBulkOpUtil mongoDbBulkOpUtil;

	/*******************************
	 * Feature bucket related beans.
	 *******************************/

	@Bean
	public FeatureBucketReader featureBucketReader() {
		return new FeatureBucketStoreMongoImpl(
				mongoTemplate,
				mongoDbBulkOpUtil);
	}

	/*****************************
	 * Smart record related beans.
	 *****************************/
}
