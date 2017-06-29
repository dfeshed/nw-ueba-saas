package fortscale.ml.model.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderServiceConfig;
import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.ml.model.retriever.factories.RetrieverFactoriesConfig;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
@Import({RetrieverFactoriesConfig.class, BucketConfigurationServiceConfig.class, FeatureBucketsReaderServiceConfig.class})
@ComponentScan(basePackageClasses = ContextHistogramRetriever.class)
public class DataReterieverFactoryConfig {
    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private FeatureBucketsReaderService featureBucketsReaderService;
    @Autowired
    private List<AbstractServiceAutowiringFactory<AbstractDataRetriever>> retrieversFactories;

    @Bean
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
        FactoryService<AbstractDataRetriever> factoryService = new FactoryService<>();
        retrieversFactories.forEach(x -> x.registerFactoryService(factoryService));
        return factoryService;
    }

}