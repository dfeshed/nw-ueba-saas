package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.factories.CategoricalFeatureValueRetrieverFactory;
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
        CategoricalFeatureValueRetrieverFactoryConfig.class,
        // common application confs
        FeatureBucketReaderConfig.class,
        AggregationEventsAccumulationDataReaderConfig.class,
})
@ComponentScan(
        value = "fortscale.ml.model.retriever.factories",
        // the custom context histogram retriever factory is used instead
        excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = ContextHistogramRetrieverFactory.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, value = CategoricalFeatureValueRetrieverFactory.class),
                @Filter(type = FilterType.REGEX, pattern = "fortscale.ml.model.retriever.factories.smart.*")
        }
)
public class DataRetrieverFactoryServiceConfig {
    @Autowired
    private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;
    @Autowired
    AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    @Bean
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
        FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
        dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
        return dataRetrieverFactoryService;
    }
}
