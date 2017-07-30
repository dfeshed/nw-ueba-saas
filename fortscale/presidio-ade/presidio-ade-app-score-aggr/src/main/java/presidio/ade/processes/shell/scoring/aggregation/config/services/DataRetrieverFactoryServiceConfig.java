package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.feature.event.store.config.AggregatedFeatureEventsMongoStoreConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collection;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        application-specific confs
        ModelScoreAggregationBucketConfigurationServiceConfig.class,
//        common application confs
        FeatureBucketReaderConfig.class,
        AccumulatedAggregatedFeatureEventStoreConfig.class,
        AggregatedFeatureEventsMongoStoreConfig.class
})
@ComponentScan("fortscale.ml.model.retriever.factories")
public class DataRetrieverFactoryServiceConfig {
    @Autowired
    @Qualifier("modelBucketConfigService")
    //used for ContextHistogramRetriever
    public BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;

    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;

    @Bean
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
        FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
        dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
        return dataRetrieverFactoryService;
    }

    @Bean
    public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
        return new AggregatedFeatureEventsReaderService(aggregatedFeatureEventsMongoStore,  accumulatedAggregatedFeatureEventStore);
    }
}