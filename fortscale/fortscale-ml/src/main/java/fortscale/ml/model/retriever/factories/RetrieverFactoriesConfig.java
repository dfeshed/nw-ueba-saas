package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.ml.model.config.DataRetrieverFactoryServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        RetrieverFactoriesConfig.class // i.e. EntityEventValueRetrieverFactory
})
@Import({DataRetrieverFactoryServiceConfig.class,BucketConfigurationServiceConfig.class})
public class RetrieverFactoriesConfig {
    @Autowired
    BucketConfigurationService bucketConfigurationService;
}
