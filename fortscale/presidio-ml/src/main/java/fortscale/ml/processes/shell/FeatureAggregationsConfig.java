package fortscale.ml.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorConfig;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.model.cache.EventModelsCacheServiceConfig;
import fortscale.ml.processes.shell.feature.aggregation.FeatureAggregationBucketConfigurationServiceConfig;
import fortscale.ml.processes.shell.feature.aggregation.InMemoryFeatureAggregatorConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringServiceConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.aggr.AggrDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.scored.feature_aggregation.ScoredFeatureAggregatedDataStoreConfig;

@Configuration
@EnableSpringConfigured
@Import({
        MongoConfig.class,
        EventModelsCacheServiceConfig.class, //TODO: why? who use it - scorer
        ModelAggregationBucketConfigurationServiceConfig.class, //TODO: why? who use it
        FeatureAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureAggregatorConfig.class,
        AggregationRecordsCreatorConfig.class,
        FeatureAggregationScoringServiceConfig.class,
        ScoredFeatureAggregatedDataStoreConfig.class,
        NullStatsServiceConfig.class,
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
    private AggrDataStore scoredFeatureAggregatedStore;

    @Bean
    public PresidioExecutionService featureAggregationBucketExecutionService() {
        return new FeatureAggregationsExecutionServiceImpl(bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureAggregationScoringService, aggregationsCreator, scoredFeatureAggregatedStore);
    }
}
