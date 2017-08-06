package presidio.ade.modeling.config;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStoreImpl;
import fortscale.accumulator.aggregation.translator.AccumulatedAggregatedFeatureEventTranslator;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStoreImpl;
import fortscale.accumulator.entityEvent.translator.AccumulatedEntityEventTranslator;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.entity.event.EntityEventDataMongoStore;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.entity.event.translator.EntityEventTranslationService;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({
		ModelingServiceFeatureBucketConfServiceConfig.class,
		ModelingServiceFeatureAggregationEventConfServiceConfig.class,
		ModelingServiceEntityEventConfServiceConfig.class
})
public class ModelingServiceDependencies {
	private static final StatsService statsService = null;

	@Value("${presidio.ade.modeling.feature.buckets.default.expire.after.seconds}")
	private long featureBucketsDefaultExpireAfterSeconds;
	@Value("${presidio.ade.modeling.event.type.field.value.aggr.event}")
	private String eventTypeFieldValueAggrEvent;
	@Value("${presidio.ade.modeling.event.type.field.value.entity.event}")
	private String eventTypeFieldValueEntityEvent;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;
	@Autowired
	private AccumulatedAggregatedFeatureEventTranslator accumulatedAggregatedFeatureEventTranslator;
	@Autowired
	private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
	@Autowired
	private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;
	@Autowired
	private EntityEventTranslationService entityEventTranslationService;
	@Autowired
	private AccumulatedEntityEventTranslator accumulatedEntityEventTranslator;
	@Autowired
	private EntityEventDataMongoStore entityEventDataMongoStore;
	@Autowired
	private AccumulatedEntityEventStore accumulatedEntityEventStore;

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
	public AccumulatedAggregatedFeatureEventTranslator accumulatedAggregatedFeatureEventTranslator() {
		return new AccumulatedAggregatedFeatureEventTranslator(aggregatedFeatureNameTranslationService);
	}

	@Bean
	public AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore() {
		return new AccumulatedAggregatedFeatureEventStoreImpl(
				mongoTemplate,
				accumulatedAggregatedFeatureEventTranslator,
				statsService);
	}

	@Bean
	public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
		return new AggregatedFeatureEventsReaderService(
				aggregatedFeatureEventsMongoStore,
				accumulatedAggregatedFeatureEventStore);
	}

	/****************************
	 * Smart event related beans.
	 ****************************/

	@Bean
	public EntityEventDataMongoStore entityEventDataMongoStore() {
		return new EntityEventDataMongoStore();
	}

	@Bean
	public EntityEventTranslationService entityEventTranslationService() {
		return new EntityEventTranslationService(eventTypeFieldValueEntityEvent);
	}

	@Bean
	public AccumulatedEntityEventTranslator accumulatedEntityEventTranslator() {
		return new AccumulatedEntityEventTranslator(entityEventTranslationService);
	}

	@Bean
	public AccumulatedEntityEventStore accumulatedEntityEventStore() {
		return new AccumulatedEntityEventStoreImpl(mongoTemplate, accumulatedEntityEventTranslator, statsService);
	}

	@Bean
	public EntityEventDataReaderService entityEventDataReaderService() {
		return new EntityEventDataReaderService(entityEventDataMongoStore, accumulatedEntityEventStore);
	}

	@Bean
	public EntityEventMongoStore entityEventMongoStore() {
		return new EntityEventMongoStore();
	}
}
