package fortscale.ml.model.config;

import fortscale.accumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.accumulator.entityEvent.store.config.AccumulatedEntityEventStoreConfig;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.aggregation.feature.event.RetentionStrategiesConfService;
import fortscale.aggregation.feature.event.config.AggregatedFeatureEventsConfServiceConfig;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import fortscale.entity.event.*;
import fortscale.entity.event.translator.EntityEventTranslationServiceConfig;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AggregatedFeatureNameTranslationServiceConfig.class,
		EntityEventTranslationServiceConfig.class,
		AccumulatedAggregatedFeatureEventStoreConfig.class,
		AccumulatedEntityEventStoreConfig.class,
		MongoDbUtilServiceConfig.class,
		AggregatedFeatureEventsConfServiceConfig.class,
		FeatureBucketReaderConfig.class
})
public class ModelBuildingDependencies {
	@Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;
	@Value("${fortscale.aggregation.bucket.conf.json.file.name}")
	private String bucketConfJsonFilePath;
	@Value("${fortscale.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
	private String bucketConfJsonOverridingFilesPath;
	@Value("${fortscale.aggregation.bucket.conf.json.additional.files.path:#{null}}")
	private String bucketConfJsonAdditionalFilesPath;

	@Bean
	public BucketConfigurationService bucketConfigurationService() {
		return new BucketConfigurationService(dataSourceFieldName, bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath,bucketConfJsonAdditionalFilesPath);
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
}
