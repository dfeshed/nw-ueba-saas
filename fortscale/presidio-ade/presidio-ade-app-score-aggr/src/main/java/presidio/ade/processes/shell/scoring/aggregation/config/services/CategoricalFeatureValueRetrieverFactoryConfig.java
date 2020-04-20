package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.metrics.CategoryRarityModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.CategoricalFeatureValueRetriever;
import fortscale.ml.model.retriever.CategoricalFeatureValueRetrieverConf;
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
public class CategoricalFeatureValueRetrieverFactoryConfig {
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketReader featureBucketReader;

    @Autowired
    private CategoryRarityModelRetrieverMetricsContainer categoryRarityModelRetrieverMetricsContainer;

    @Bean
    public AbstractServiceAutowiringFactory<AbstractDataRetriever> contextSequentialReducedHistogramRetrieverFactory() {
        return new AbstractServiceAutowiringFactory<AbstractDataRetriever>() {
            @Override
            public String getFactoryName() {
                return CategoricalFeatureValueRetrieverConf.FACTORY_NAME;
            }

            @Override
            public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
                CategoricalFeatureValueRetrieverConf config = (CategoricalFeatureValueRetrieverConf)factoryConfig;
                return new CategoricalFeatureValueRetriever(config, bucketConfigurationService, featureBucketReader, categoryRarityModelRetrieverMetricsContainer);
            }
        };
    }
}
