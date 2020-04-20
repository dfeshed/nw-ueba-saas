package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.processes.shell.aggregation.LevelThreeAggregationsService;
import presidio.ade.processes.shell.config.*;
import presidio.monitoring.flush.MetricContainerFlusher;
import presidio.monitoring.flush.MetricContainerFlusherConfig;

@Configuration
@Import({
        // Application specific configurations
        EventModelsCacheServiceConfig.class,
        InMemoryFeatureAggregatorConfig.class,
        AggregationRecordsCreatorConfig.class,
        FeatureAggregationScoringServiceConfig.class,
        LevelThreeAggregationsConfig.class,
        // Common application configurations
        EnrichedDataStoreConfig.class,
        MetricContainerFlusherConfig.class,
        AggregatedDataStoreConfig.class,
        StoreManagerConfig.class
})
public class FeatureAggregationsConfiguration {
    @Autowired
    @Qualifier("bucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    @Autowired
    private FeatureAggregationScoringService featureAggregationScoringService;
    @Autowired
    private AggregationRecordsCreator aggregationsCreator;
    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    private StoreManager storeManager;
    @Value("${feature.aggregation.pageIterator.pageSize}")
    private int pageSize;
    @Value("${feature.aggregation.pageIterator.maxGroupSize}")
    private int maxGroupSize;
    @Autowired
    private MetricContainerFlusher metricContainerFlusher;
    @Autowired
    private LevelThreeAggregationsService levelThreeAggregationsService;

    @Bean
    public PresidioExecutionService featureAggregationBucketExecutionService() {
        return new FeatureAggregationsExecutionServiceImpl(
                bucketConfigurationService,
                enrichedDataStore,
                inMemoryFeatureBucketAggregator,
                featureAggregationScoringService,
                aggregationsCreator,
                aggregatedDataStore,
                storeManager,
                pageSize,
                maxGroupSize,
                metricContainerFlusher,
                levelThreeAggregationsService);
    }
}
