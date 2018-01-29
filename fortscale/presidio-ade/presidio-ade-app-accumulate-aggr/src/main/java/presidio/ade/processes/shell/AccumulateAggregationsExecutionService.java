package presidio.ade.processes.shell;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.common.general.Schema;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketService;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsService;

import java.time.Instant;

/**
 * Created by maria_dorohin on 7/26/17.
 */
public class AccumulateAggregationsExecutionService {
    private static String SCHEMA = "schema";
    private static String FIXED_DURATION_STRATEGY = "fixed_duration_strategy";

    private final BucketConfigurationService bucketConfigurationService;
    private final EnrichedDataStore enrichedDataStore;
    private final AggregationEventsAccumulationDataStore aggregationEventsAccumulationDataStore;
    private final int pageSize;
    private final int maxGroupSize;
    private final AccumulateAggregationsBucketService accumulateAggregationsBucketService;
    private final AccumulationsCache accumulationsCache;
    private final StoreManager storeManager;

    public AccumulateAggregationsExecutionService(BucketConfigurationService bucketConfigurationService,
                                                  EnrichedDataStore enrichedDataStore,
                                                  AggregationEventsAccumulationDataStore aggregationEventsAccumulationDataStore,
                                                  AccumulateAggregationsBucketService accumulateAggregationsBucketService,
                                                  AccumulationsCache accumulationsCache, StoreManager storeManager, int pageSize, int maxGroupSize) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.aggregationEventsAccumulationDataStore = aggregationEventsAccumulationDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.accumulateAggregationsBucketService = accumulateAggregationsBucketService;
        this.accumulationsCache = accumulationsCache;
        this.storeManager = storeManager;
    }

    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration, Double featureBucketStrategy) throws Exception {
        //strategy for accumulator
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        //strategy for aggregator
        FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(featureBucketStrategy.longValue());
        AccumulateAggregationsService featureAggregationBucketsService = new AccumulateAggregationsService(fixedDurationStrategy, bucketConfigurationService, enrichedDataStore, aggregationEventsAccumulationDataStore, pageSize, maxGroupSize, strategy, accumulateAggregationsBucketService, accumulationsCache);
        TimeRange timeRange = new TimeRange(startDate, endDate);

        StoreManagerMetadataProperties storeManagerMetadataProperties = createStoreManagerAwareMetadata(schema, fixedDurationStrategy);

        featureAggregationBucketsService.execute(timeRange, schema.getName(), storeManagerMetadataProperties);
        storeManager.cleanupCollections(storeManagerMetadataProperties.getProperties(), startDate);
    }

    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }

    public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration, Double featureBucketStrategy) throws Exception {
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

