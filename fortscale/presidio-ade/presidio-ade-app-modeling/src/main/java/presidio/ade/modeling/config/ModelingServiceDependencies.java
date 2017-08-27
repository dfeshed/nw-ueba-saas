package presidio.ade.modeling.config;

import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;
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
	private static final StatsService statsService = null;

	@Value("${presidio.ade.modeling.feature.buckets.default.expire.after.seconds}")
	private long featureBucketsDefaultExpireAfterSeconds;
	@Value("${presidio.ade.modeling.event.type.field.value.aggr.event}")
	private String eventTypeFieldValueAggrEvent;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
	@Autowired
	private SmartRecordConfService smartRecordConfService;
	@Autowired
	private SmartAccumulationDataReader smartAccumulationDataReader;
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

	/****************************
	 * Smart event related beans.
	 ****************************/
}
