package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.metrics.TimeModelRetrieverMetricsContainer;
import fortscale.ml.model.metrics.TimeModelRetrieverMetricsContainerConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.ml.model.retriever.factories.ContextHistogramRetrieverFactory;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The original {@link ContextHistogramRetrieverFactory} autowires a {@link BucketConfigurationService}, but the score
 * aggregations application has two feature bucket configuration services (one for the enriched record models and one
 * for the score aggregation events). Since the original factory wouldn't know which of the two services needs to be
 * injected, this configuration class autowires the appropriate service (the one for the enriched record models) and
 * creates a custom context histogram retriever factory.
 *
 * @author Lior Govrin
 */
@Configuration
@Import(TimeModelRetrieverMetricsContainerConfig.class)
public class CustomContextHistogramRetrieverFactoryConfig {
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    private TimeModelRetrieverMetricsContainer timeModelRetrieverMetricsContainer;

    @Bean
    public AbstractServiceAutowiringFactory<AbstractDataRetriever> customContextHistogramRetrieverFactory() {
        return new AbstractServiceAutowiringFactory<AbstractDataRetriever>() {
            @Override
            public String getFactoryName() {
                return ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER;
            }

            @Override
            public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
                ContextHistogramRetrieverConf config = (ContextHistogramRetrieverConf)factoryConfig;
                return new ContextHistogramRetriever(config, bucketConfigurationService, featureBucketReader, timeModelRetrieverMetricsContainer);
            }
        };
    }
}
