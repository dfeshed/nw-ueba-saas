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
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.FeatureBucketContextSelectorFactory;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
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
            basePackages = "fortscale.ml.model.retriever,fortscale.ml.model.selector,fortscale.ml.model.builder"
    )
    static class ContextConfiguration {
        @Mock
        private EntityEventDataReaderService entityEventDataReaderService;
        @Mock
        private FeatureBucketsReaderService featureBucketsReaderService;
        @Mock
        private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

        @Bean
        public AggregatedFeatureEventsReaderService getAggregatedFeatureEventsReaderService() {
            return aggregatedFeatureEventsReaderService;
        }
        @Bean
        public FeatureBucketsReaderService getFeatureBucketsReaderService() {
            return featureBucketsReaderService;
        }
        @Bean
        public EntityEventDataReaderService getEntityEventDataReaderService() {
            return entityEventDataReaderService;
        }
        @Bean
        public RetentionStrategiesConfService retentionStrategiesConfService() {return new RetentionStrategiesConfService();}
        @Bean
        public BucketConfigurationService bucketConfigurationService() {
            return new BucketConfigurationService();
        }
        @Bean
        public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {return new AggregatedFeatureEventsConfService();}
        @Bean
        public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService() {return new AggregatedFeatureEventsConfUtilService();}
        @Bean
        public EntityEventConfService entityEventConfService() {
            return new EntityEventConfService();
        }
        @Bean
        public EntityEventGlobalParamsConfService entityEventGlobalParamsConfService() {return new EntityEventGlobalParamsConfService();}
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
        public FeatureBucketContextSelectorFactory featureBucketContextSelectorFactory() {
            return new FeatureBucketContextSelectorFactory();
        }
        @Bean
        public PropertyPlaceholderConfigurer propertyConfigurer() throws IOException {
            PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
            props.setLocations(new Resource[]{new ClassPathResource("ModelConfProductionConfFilesTest.properties")});
            return props;
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
        int counter = 0;

        for (ModelConf modelConf : modelConfService.getModelConfs()) {
            IContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();
            if (contextSelectorConf != null)
                contextSelectorFactoryService.getProduct(contextSelectorConf);
            dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
            modelBuilderFactoryService.getProduct(modelConf.getModelBuilderConf());
            counter++;
        }

        int expRawEventsModelConfs = 52;
        int expAggrEventsModelConfs = 64;
        int expEntityEventsModelConfs = 2;
        Assert.assertEquals(expRawEventsModelConfs + expAggrEventsModelConfs + expEntityEventsModelConfs, counter);
    }
}
