package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorConfig;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.model.cache.EventModelsCacheServiceConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringServiceConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.scored.feature_aggregation.ScoredFeatureAggregationDataStoreConfig;
import presidio.ade.processes.shell.aggregation.FeatureAggregationBucketConfigurationServiceConfig;
import presidio.ade.processes.shell.aggregation.InMemoryFeatureAggregatorConfig;

@Configuration
@Import({
        EventModelsCacheServiceConfig.class,
        ModelAggregationBucketConfigurationServiceConfig.class,
        FeatureAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureAggregatorConfig.class,
        AggregationRecordsCreatorConfig.class,
        FeatureAggregationScoringServiceConfig.class,
        ScoredFeatureAggregationDataStoreConfig.class,
        NullStatsServiceConfig.class, // TODO: Remove this
})
public class FeatureAggregationsConfig {


    @Autowired
    @Qualifier("featureAggregationBucketConfigService")
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
    @Value("${feature.aggregation.pageSize}")
    private int pageSize;
    @Value("${feature.aggregation.maxGroupSize}")
    private int maxGroupSize;

    @Bean
    public PresidioExecutionService featureAggregationBucketExecutionService() {
        return new FeatureAggregationsExecutionServiceImpl(bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureAggregationScoringService, aggregationsCreator, scoredFeatureAggregatedStore, pageSize, maxGroupSize);
    }
}
