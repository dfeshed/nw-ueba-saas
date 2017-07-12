package fortscale.ml.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoConfig;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.processes.shell.model.aggregation.InMemoryFeatureBucketAggregatorConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;

@Configuration
@EnableSpringConfigured
@Import({
        MongoConfig.class,
        ModelAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        FeatureBucketStoreMongoConfig.class
})
public class ModelFeatureAggregationBucketsConfiguration {
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    @Autowired
    private FeatureBucketStore featureBucketStore;

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        return new ModelFeatureAggregationBucketsExecutionServiceImpl(
                bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureBucketStore);
    }
}
