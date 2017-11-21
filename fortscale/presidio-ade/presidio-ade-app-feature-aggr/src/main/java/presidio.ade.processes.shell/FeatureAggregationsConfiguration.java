package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
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
import presidio.ade.processes.shell.config.AggregationRecordsCreatorConfig;
import presidio.ade.processes.shell.config.EventModelsCacheServiceConfig;
import presidio.ade.processes.shell.config.FeatureAggregationScoringServiceConfig;
import presidio.ade.processes.shell.config.InMemoryFeatureAggregatorConfig;

@Configuration
@Import({
        //        application-specific confs
        EventModelsCacheServiceConfig.class,
        InMemoryFeatureAggregatorConfig.class,
        AggregationRecordsCreatorConfig.class,
        FeatureAggregationScoringServiceConfig.class,
        //        common application confs
        EnrichedDataStoreConfig.class,
        AggregatedDataStoreConfig.class,
        StoreManagerConfig.class,
        NullStatsServiceConfig.class, // TODO: Remove this
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
    private AggregationRecordsCreator aggregationsCreator;
    @Autowired
    private FeatureAggregationScoringService featureAggregationScoringService;
    @Autowired
    private AggregatedDataStore scoredFeatureAggregatedStore;
    @Value("${feature.aggregation.pageIterator.pageSize}")
    private int pageSize;
    @Value("${feature.aggregation.pageIterator.maxGroupSize}")
    private int maxGroupSize;
    @Autowired
    private StoreManager storeManager;

    @Bean
    public PresidioExecutionService featureAggregationBucketExecutionService() {
        return new FeatureAggregationsExecutionServiceImpl(bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureAggregationScoringService, aggregationsCreator, scoredFeatureAggregatedStore, storeManager, pageSize, maxGroupSize);
    }
}
