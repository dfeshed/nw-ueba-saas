package fortscale.ml.model;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.aggregation.feature.event.RetentionStrategiesConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataMongoStore;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.entity.event.EntityEventGlobalParamsConfService;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureValueRetrieverFactory;
import fortscale.ml.model.selector.*;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelConfProductionConfFilesTest {
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
    @Configuration
    @ComponentScan(
            basePackages = "fortscale.ml.model.retriever,fortscale.ml.model.selector,fortscale.ml.model.builder,fortscale.aggregation"
    )
    static class ContextConfiguration {
        @Bean
        public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {return new AggregatedFeatureEventsReaderService();}
        @Bean
        public RetentionStrategiesConfService retentionStrategiesConfService() {return new RetentionStrategiesConfService();}
        @Bean
        public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {return new AggregatedFeatureEventsConfService();}
        @Bean
        public FeatureBucketsMongoStore featureBucketsMongoStore() {return Mockito.mock(FeatureBucketsMongoStore.class);}
        @Bean
        public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService() {return new AggregatedFeatureEventsConfUtilService();}
        @Bean
        public FeatureBucketsReaderService featureBucketsReaderService() {return new FeatureBucketsReaderService();}
        @Bean
        public EntityEventConfService entityEventConfService() {
            return new EntityEventConfService();
        }
        @Bean
        public EntityEventGlobalParamsConfService entityEventGlobalParamsConfService() {return new EntityEventGlobalParamsConfService();}
        @Bean
        public EntityEventDataReaderService entityEventDataReaderService() {return new EntityEventDataReaderService();}
        @Bean
        public EntityEventDataMongoStore entityEventDataMongoStore() {return Mockito.mock(EntityEventDataMongoStore.class);}
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
        public FactoryService<IModelBuilder> modelBuilderFactoryService() {return new FactoryService<>();}
        @Bean
        public AggregatedEventContextSelectorFactory aggregatedEventContextSelectorFactory(){return new AggregatedEventContextSelectorFactory();}
        @Bean
        public AggregatedFeatureValueRetrieverFactory aggregatedFeatureValueRetrieverFactory(){return new AggregatedFeatureValueRetrieverFactory();}
        @Bean
        public ModelStore modelStore() {
            return Mockito.mock(ModelStore.class);
        }
        @Bean
        public MongoTemplate mongoTemplate() {
            return Mockito.mock(MongoTemplate.class);
        }
        @Bean
        public MongoDbUtilService mongoDbUtilService() {
            return Mockito.mock(MongoDbUtilService.class);
        }
        @Bean
        public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore() {return Mockito.mock(AggregatedFeatureEventsMongoStore.class);}
        @Bean
        public BucketConfigurationService bucketConfigurationService() {
            return new BucketConfigurationService();
        }
        @Bean
        public PropertyPlaceholderConfigurer propertyConfigurer() throws IOException {
            PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
            props.setLocations(new Resource[]{new ClassPathResource("ModelConfProductionConfFilesTest.properties")});
            return props;
        }

        @Bean
        public FeatureBucketContextSelectorFactory featureBucketContextSelectorFactory() {
            return new FeatureBucketContextSelectorFactory();
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

    private IContextSelector contextSelector;
    private AbstractDataRetriever dataRetriever;
    private IModelBuilder modelBuilder;

    @Test
    public void ShouldBeValidConf() {
        for (ModelConf modelConf : modelConfService.getModelConfs()) {
            IContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();
            contextSelector = contextSelectorConf == null ? null : contextSelectorFactoryService.getProduct(contextSelectorConf);
            dataRetriever = dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
            modelBuilder = modelBuilderFactoryService.getProduct(modelConf.getModelBuilderConf());
        }
    }


}
