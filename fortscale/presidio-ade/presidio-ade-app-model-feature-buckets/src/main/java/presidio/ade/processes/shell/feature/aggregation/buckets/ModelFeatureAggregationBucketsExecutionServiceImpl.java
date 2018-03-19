package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.time.Instant;

public class ModelFeatureAggregationBucketsExecutionServiceImpl extends PresidioExecutionService {
    private static final String SCHEMA = "schema";
    private static final String FIXED_DURATION_STRATEGY = "fixed_duration_strategy";

    private final int maxGroupSize;
    private final int pageSize;
    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private FeatureBucketStore featureBucketStore;
    private StoreManager storeManager;

    public ModelFeatureAggregationBucketsExecutionServiceImpl(
            BucketConfigurationService bucketConfigurationService,
            EnrichedDataStore enrichedDataStore,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            FeatureBucketStore featureBucketStore, StoreManager storeManager, int pageSize, int maxGroupSize) {

        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.featureBucketStore = featureBucketStore;
        this.storeManager = storeManager;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
    }

    public void run(Schema schema, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
        ModelFeatureAggregationBucketsService service = new ModelFeatureAggregationBucketsService(
                bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureBucketStore, pageSize, maxGroupSize);
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(schema, fixedDurationStrategy);

        service.execute(new TimeRange(startInstant, endInstant), schema.getName(), storeMetadataProperties);
        storeManager.cleanupCollections(storeMetadataProperties, startInstant);
    }

    public void applyRetentionPolicy(Schema schema, Instant startInstant, Instant endInstant) throws Exception {
        // TODO: Implement
    }

    public void cleanAll(Schema schema) throws Exception {
        // TODO: Implement
    }

    public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(schema, fixedDurationStrategy);
        storeManager.cleanupCollections(storeMetadataProperties, startDate, endDate);
    }


    private StoreMetadataProperties createStoreMetadataProperties(Schema schema, FixedDurationStrategy fixedDurationStrategy){
        StoreMetadataProperties storeMetadataProperties = new StoreMetadataProperties();
        storeMetadataProperties.setProperty(SCHEMA, schema.getName());
        storeMetadataProperties.setProperty(FIXED_DURATION_STRATEGY, fixedDurationStrategy.toStrategyName());
        return storeMetadataProperties;
    }

}
