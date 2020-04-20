package presidio.ade.processes.shell.feature.aggregation.buckets.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoConfig;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.processes.shell.feature.aggregation.buckets.ModelFeatureAggregationBucketsExecutionServiceImpl;

@Configuration
@EnableSpringConfigured
@Import({
        ModelAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        FeatureBucketStoreMongoConfig.class,
        StoreManagerConfig.class,
})
public class ModelFeatureAggregationBucketsConfiguration {

    @Value("${model-feature-aggregation.pageIterator.pageSize}")
    private int pageSize;
    @Value("${model-feature-aggregation.pageIterator.maxGroupSize}")
    private int maxGroupSize;

    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    @Autowired
    private FeatureBucketStore featureBucketStore;
    @Autowired
    private StoreManager storeManager;

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        return new ModelFeatureAggregationBucketsExecutionServiceImpl(
                bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureBucketStore, storeManager, pageSize, maxGroupSize);
    }
}
