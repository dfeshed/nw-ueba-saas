package fortscale.ml.model.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.aggregation.feature.event.RetentionStrategiesConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureNameTranslationService;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.entity.event.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelBuildingDependencies {

	@Value("${streaming.event.field.type.aggr_event}")
	private String eventType;

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

	@Bean
	public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {
		return new AggregatedFeatureEventsConfService();
	}

	@Bean
	public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService() {
		return new AggregatedFeatureEventsConfUtilService();
	}

	@Bean
	public RetentionStrategiesConfService retentionStrategiesConfService() {
		return new RetentionStrategiesConfService();
	}

	@Bean
	public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
		return new AggregatedFeatureEventsReaderService();
	}

	@Bean
	public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore() {
		return new AggregatedFeatureEventsMongoStore();
	}

	@Bean
	public EntityEventConfService entityEventConfService() {
		return new EntityEventConfService();
	}

	@Bean
	public EntityEventGlobalParamsConfService entityEventGlobalParamsConfService() {
		return new EntityEventGlobalParamsConfService();
	}

	@Bean
	public EntityEventDataReaderService entityEventDataReaderService() {
		return new EntityEventDataReaderService();
	}

	@Bean
	public EntityEventMongoStore entityEventMongoStore() {
		return new EntityEventMongoStore();
	}

	@Bean
	public EntityEventDataMongoStore entityEventDataMongoStore() {
		return new EntityEventDataMongoStore();
	}

	@Bean
	public AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService() {
		return new AggregatedFeatureNameTranslationService(eventType);
	}
}
