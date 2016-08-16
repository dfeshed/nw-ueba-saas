package fortscale.ml.model;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.aggregation.feature.event.RetentionStrategiesConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.entity.event.EntityEventGlobalParamsConfService;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelConfProductionConfFilesTest {
	@Configuration
	@EnableSpringConfigured
	@EnableAnnotationConfiguration
	@ComponentScan(basePackages = "fortscale.ml.model.selector,fortscale.ml.model.retriever,fortscale.ml.model.builder")
	@Import(NullStatsServiceConfig.class)
	static class ContextConfiguration {
		@Mock private FeatureBucketsReaderService featureBucketsReaderService;
		@Mock private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;
		@Mock private EntityEventDataReaderService entityEventDataReaderService;
		@Mock private EntityEventMongoStore entityEventMongoStore;
		@Mock private ModelStore modelStore;

		@Bean public FeatureBucketsReaderService getFeatureBucketsReaderService() {return featureBucketsReaderService;}
		@Bean public AggregatedFeatureEventsReaderService getAggregatedFeatureEventsReaderService() {return aggregatedFeatureEventsReaderService;}
		@Bean public EntityEventDataReaderService getEntityEventDataReaderService() {return entityEventDataReaderService;}
		@Bean public EntityEventMongoStore getEntityEventMongoStore() {return entityEventMongoStore;}
		@Bean public ModelStore getModelStore() {return modelStore;}

		@Bean
		public BucketConfigurationService bucketConfigurationService() {
			return new BucketConfigurationService();
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
		public EntityEventConfService entityEventConfService() {
			return new EntityEventConfService();
		}

		@Bean
		public EntityEventGlobalParamsConfService entityEventGlobalParamsConfService() {
			return new EntityEventGlobalParamsConfService();
		}

		@Bean
		public ModelConfService modelConfService() {
			return new ModelConfService();
		}

		@Bean
		public FactoryService<IContextSelector> contextSelectorFactoryService() {
			return new FactoryService<>();
		}

		@Bean
		public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
			return new FactoryService<>();
		}

		@Bean
		public FactoryService<IModelBuilder> modelBuilderFactoryService() {
			return new FactoryService<>();
		}

		@Bean
		public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("impala.table.fields.data.source", "data_source");
			properties.put("fortscale.aggregation.bucket.conf.json.file.name", "classpath:config/asl/buckets.json");
			properties.put("fortscale.aggregation.bucket.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/buckets/overriding/*.json");
			properties.put("fortscale.aggregation.bucket.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/buckets/additional/*.json");

			properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregated_feature_events.json");
			properties.put("fortscale.aggregation.feature.event.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/aggregation_events/overriding/*.json");
			properties.put("fortscale.aggregation.feature.event.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/aggregation_events/additional/*.json");
			properties.put("streaming.event.field.type.aggr_event", "aggr_event");
			properties.put("streaming.aggr_event.field.context", "context");
			properties.put("fortscale.aggregation.retention.strategy.conf.json.file.name", "classpath:config/asl/retention_strategies.json");
			properties.put("fortscale.aggregation.retention.strategy.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/retention_strategy/overriding/*.json");
			properties.put("fortscale.aggregation.retention.strategy.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/retention_strategy/additional/*.json");

			properties.put("fortscale.entity.event.definitions.json.file.path", "classpath:config/asl/entity_events.json");
			properties.put("fortscale.entity.event.definitions.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events*.json");
			properties.put("fortscale.entity.event.global.params.json.file.path", "classpath:config/asl/entity_events_global_params.json");
			properties.put("fortscale.entity.event.global.params.conf.json.overriding.files.path", "file:home/cloudera/config/asl/entity_events/overriding/global_params*.json");

			properties.put("fortscale.model.configurations.location.path", "classpath:config/asl/models/*.json");
			properties.put("fortscale.model.configurations.overriding.location.path", "file:home/cloudera/fortscale/config/asl/models/overriding/*.json");
			properties.put("fortscale.model.configurations.additional.location.path", "file:home/cloudera/fortscale/config/asl/models/additional/*.json");

			PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
			propertyPlaceholderConfigurer.setProperties(properties);
			return propertyPlaceholderConfigurer;
		}
	}

	@Autowired
	private ModelConfService modelConfService;
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private FactoryService<IModelBuilder> modelBuilderFactoryService;

	@Test
	public void ShouldBeValidConf() {
		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			IContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();
			if (contextSelectorConf != null) contextSelectorFactoryService.getProduct(contextSelectorConf);
			dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
			modelBuilderFactoryService.getProduct(modelConf.getModelBuilderConf());
		}
	}
}
