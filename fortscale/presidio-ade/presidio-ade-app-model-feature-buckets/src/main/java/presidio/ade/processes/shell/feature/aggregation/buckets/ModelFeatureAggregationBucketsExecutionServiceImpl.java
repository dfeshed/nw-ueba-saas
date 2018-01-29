package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.time.Instant;

public class ModelFeatureAggregationBucketsExecutionServiceImpl implements PresidioExecutionService {
    private static String SCHEMA = "schema";
    private static String FIXED_DURATION_STRATEGY = "fixed_duration_strategy";

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

    @Override
    public void run(Schema schema, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
        ModelFeatureAggregationBucketsService service = new ModelFeatureAggregationBucketsService(
                bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureBucketStore, pageSize, maxGroupSize);
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
        StoreManagerMetadataProperties storeManagerMetadataProperties = createStoreManagerAwareMetadata(schema, fixedDurationStrategy);

        service.execute(new TimeRange(startInstant, endInstant), schema.getName(), storeManagerMetadataProperties);
        storeManager.cleanupCollections(storeManagerMetadataProperties.getProperties(), startInstant);
    }

    @Override
    public void applyRetentionPolicy(Schema schema, Instant startInstant, Instant endInstant) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        StoreManagerMetadataProperties storeManagerMetadataProperties = createStoreManagerAwareMetadata(schema, fixedDurationStrategy);
        storeManager.cleanupCollections(storeManagerMetadataProperties.getProperties(), startDate, endDate);
    }


    private StoreManagerMetadataProperties createStoreManagerAwareMetadata(Schema schema, FixedDurationStrategy fixedDurationStrategy){
        StoreManagerMetadataProperties storeManagerMetadataProperties = new StoreManagerMetadataProperties();
        storeManagerMetadataProperties.setProperties(SCHEMA, schema.getName());
        storeManagerMetadataProperties.setProperties(FIXED_DURATION_STRATEGY, fixedDurationStrategy.toStrategyName());
        return storeManagerMetadataProperties;
    }

}
