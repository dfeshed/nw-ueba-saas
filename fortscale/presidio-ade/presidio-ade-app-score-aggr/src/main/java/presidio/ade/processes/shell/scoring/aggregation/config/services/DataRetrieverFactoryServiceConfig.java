package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.aggregation.feature.event.store.config.AggregatedFeatureEventsMongoStoreConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.factories.ContextHistogramRetrieverFactory;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;

import java.util.Collection;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
        // application-specific confs
        ModelScoreAggregationBucketConfigurationServiceConfig.class,
        CustomContextHistogramRetrieverFactoryConfig.class,
        // common application confs
        FeatureBucketReaderConfig.class,
        AggregationEventsAccumulationDataReaderConfig.class,
        AggregatedFeatureEventsMongoStoreConfig.class
})
@ComponentScan(
        value = "fortscale.ml.model.retriever.factories",
        // the custom context histogram retriever factory is used instead
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = ContextHistogramRetrieverFactory.class)
)
public class DataRetrieverFactoryServiceConfig {
    @Autowired
    private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    @Bean
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
        FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
        dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
        return dataRetrieverFactoryService;
    }

    @Bean
    public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
        return new AggregatedFeatureEventsReaderService(aggregatedFeatureEventsMongoStore, aggregationEventsAccumulationDataReader);
    }
}
