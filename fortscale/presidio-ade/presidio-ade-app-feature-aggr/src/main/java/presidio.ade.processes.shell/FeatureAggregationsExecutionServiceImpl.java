package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.aggregation.FeatureAggregationService;
import presidio.ade.processes.shell.aggregation.LevelThreeAggregationsService;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.time.Instant;

public class FeatureAggregationsExecutionServiceImpl implements PresidioExecutionService {
    private static final String SCHEMA = "schema";
    private static final String FIXED_DURATION_STRATEGY = "fixed_duration_strategy";

    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private FeatureAggregationScoringService featureAggregationScoringService;
    private AggregationRecordsCreator featureAggregationsCreator;
    private AggregatedDataStore scoredFeatureAggregatedStore;
    private StoreManager storeManager;
    private int pageSize;
    private int maxGroupSize;
    private MetricContainerFlusher metricContainerFlusher;
    private LevelThreeAggregationsService levelThreeAggregationsService;

    public FeatureAggregationsExecutionServiceImpl(
            BucketConfigurationService bucketConfigurationService,
            EnrichedDataStore enrichedDataStore,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            FeatureAggregationScoringService featureAggregationScoringService,
            AggregationRecordsCreator featureAggregationsCreator,
            AggregatedDataStore scoredFeatureAggregatedStore,
            StoreManager storeManager,
            int pageSize,
            int maxGroupSize,
            MetricContainerFlusher metricContainerFlusher,
            LevelThreeAggregationsService levelThreeAggregationsService) {

        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.featureAggregationScoringService = featureAggregationScoringService;
        this.featureAggregationsCreator = featureAggregationsCreator;
        this.scoredFeatureAggregatedStore = scoredFeatureAggregatedStore;
        this.storeManager = storeManager;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.metricContainerFlusher = metricContainerFlusher;
        this.levelThreeAggregationsService = levelThreeAggregationsService;
    }

    // TODO: Data source should be event_type
    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        FeatureAggregationService featureAggregationBucketsService = new FeatureAggregationService(
                fixedDurationStrategy,
                bucketConfigurationService,
                enrichedDataStore,
                inMemoryFeatureBucketAggregator,
                featureAggregationScoringService,
                featureAggregationsCreator,
                scoredFeatureAggregatedStore,
                pageSize,
                maxGroupSize,
                metricContainerFlusher,
                levelThreeAggregationsService);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(schema, fixedDurationStrategy);
        featureAggregationBucketsService.execute(timeRange, schema.getName(), storeMetadataProperties);
        storeManager.cleanupCollections(storeMetadataProperties, startDate);
    }

    @Override
    public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(schema, fixedDurationStrategy);
        storeManager.cleanupCollections(storeMetadataProperties, startDate, endDate);
    }

    @Override
    public void applyRetentionPolicy(Schema schema, Instant startDate, Instant endDate) {
        // TODO: Implement
    }

    @Override
    public void cleanAll(Schema schema) {
        // TODO: Implement
    }

    private StoreMetadataProperties createStoreMetadataProperties(Schema schema, FixedDurationStrategy fixedDurationStrategy) {
        StoreMetadataProperties storeMetadataProperties = new StoreMetadataProperties();
        storeMetadataProperties.setProperty(SCHEMA, schema.getName());
        storeMetadataProperties.setProperty(FIXED_DURATION_STRATEGY, fixedDurationStrategy.toStrategyName());
        return storeMetadataProperties;
    }
}
