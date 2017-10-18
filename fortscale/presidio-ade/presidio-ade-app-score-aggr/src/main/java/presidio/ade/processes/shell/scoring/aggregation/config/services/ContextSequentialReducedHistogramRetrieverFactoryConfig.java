package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextSequentialReducedHistogramRetriever;
import fortscale.ml.model.retriever.ContextSequentialReducedHistogramRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/17/17.
 */
@Configuration
public class ContextSequentialReducedHistogramRetrieverFactoryConfig {
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketReader featureBucketReader;

    @Bean
    public AbstractServiceAutowiringFactory<AbstractDataRetriever> contextSequentialReducedHistogramRetrieverFactory() {
        return new AbstractServiceAutowiringFactory<AbstractDataRetriever>() {
            @Override
            public String getFactoryName() {
                return ContextSequentialReducedHistogramRetrieverConf.CONTEXT_HISTOGRAM_SEQUENTIAL_REDUCED_RETRIEVER;
            }

            @Override
            public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
                ContextSequentialReducedHistogramRetrieverConf config = (ContextSequentialReducedHistogramRetrieverConf)factoryConfig;
                return new ContextSequentialReducedHistogramRetriever(config, bucketConfigurationService, featureBucketReader);
            }
        };
    }
}
