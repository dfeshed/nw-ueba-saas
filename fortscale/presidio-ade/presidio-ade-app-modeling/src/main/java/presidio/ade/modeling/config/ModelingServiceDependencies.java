package presidio.ade.modeling.config;

import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
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

import java.util.Collections;

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
	private AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;
	@Autowired
	private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
	@Autowired
	private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

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

	/******************************************
	 * Feature aggregation event related beans.
	 ******************************************/

	@Bean
	public AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService() {
		return new AggregatedFeatureNameTranslationService(eventTypeFieldValueAggrEvent);
	}

	@Bean
	public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore() {
		return new AggregatedFeatureEventsMongoStore(
				mongoTemplate,
				aggregatedFeatureEventsConfService,
				statsService,
				aggregatedFeatureNameTranslationService,
				Collections.emptyList());
	}

	@Bean
	public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
		return new AggregatedFeatureEventsReaderService(
				aggregatedFeatureEventsMongoStore,
				aggregationEventsAccumulationDataReader);
	}

	/****************************
	 * Smart event related beans.
	 ****************************/
}
