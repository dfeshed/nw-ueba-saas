package presidio.ade.processes.shell.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;

import java.util.Collection;

/**
 * Created by barak_schuster on 7/30/17.
 */

@Configuration
@Import({
//        application-specific confs
        ModelFeatureAggregationBucketConfigurationServiceConfig.class,
//        common application confs
        AggregationEventsAccumulationDataReaderConfig.class,
        FeatureBucketReaderConfig.class
})
@ComponentScan("fortscale.ml.model.retriever.factories")
public class FeatureAggregationDataRetrieverFactoryServiceConfig {
    @Autowired
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
    @Autowired
    @Qualifier("modelBucketConfigService")
    //used for ContextHistogramRetriever
    public BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;

    @Bean
    public FactoryService<AbstractDataRetriever> fadataRetrieverFactoryService() {
        FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
        dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
        return dataRetrieverFactoryService;
    }
}