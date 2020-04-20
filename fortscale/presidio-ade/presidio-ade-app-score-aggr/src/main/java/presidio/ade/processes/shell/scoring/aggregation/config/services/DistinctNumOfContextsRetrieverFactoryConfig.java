package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.metrics.CategoryRarityModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.*;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistinctNumOfContextsRetrieverFactoryConfig {
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketReader featureBucketReader;


    @Bean
    public AbstractServiceAutowiringFactory<AbstractDataRetriever> distinctNumOfContextsRetrieverFactory() {
        return new AbstractServiceAutowiringFactory<AbstractDataRetriever>() {
            @Override
            public String getFactoryName() {
                return DistinctNumOfContextsRetrieverConf.FACTORY_NAME;
            }

            @Override
            public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
                DistinctNumOfContextsRetrieverConf config = (DistinctNumOfContextsRetrieverConf)factoryConfig;
                return new DistinctNumOfContextsRetriever(config, bucketConfigurationService, featureBucketReader);
            }
        };
    }
}
