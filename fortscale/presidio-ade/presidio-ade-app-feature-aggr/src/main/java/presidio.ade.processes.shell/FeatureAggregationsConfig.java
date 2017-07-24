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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.scored.feature_aggregation.ScoredFeatureAggregatedDataStoreConfig;
import presidio.ade.processes.shell.feature.aggregation.FeatureAggregationBucketConfigurationServiceConfig;
import presidio.ade.processes.shell.feature.aggregation.InMemoryFeatureAggregatorConfig;

@Configuration
//@EnableSpringConfigured
@Import({
        EventModelsCacheServiceConfig.class,
        ModelAggregationBucketConfigurationServiceConfig.class,
        FeatureAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureAggregatorConfig.class,
        AggregationRecordsCreatorConfig.class,
        FeatureAggregationScoringServiceConfig.class,
        ScoredFeatureAggregatedDataStoreConfig.class,
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

    @Bean
    public PresidioExecutionService featureAggregationBucketExecutionService() {
        return new FeatureAggregationsExecutionServiceImpl(bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureAggregationScoringService, aggregationsCreator, scoredFeatureAggregatedStore);
    }
}
