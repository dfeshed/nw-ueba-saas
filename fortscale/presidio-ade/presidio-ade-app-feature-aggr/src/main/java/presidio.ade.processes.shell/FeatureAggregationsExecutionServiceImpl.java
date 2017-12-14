package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.ml.scorer.metrics.ScoringServiceMetricsContainer;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.StoreManager;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.aggregation.FeatureAggregationService;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.time.Instant;

public class FeatureAggregationsExecutionServiceImpl implements PresidioExecutionService {
    private final MetricContainerFlusher metricContainerFlusher;
    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private FeatureAggregationScoringService featureAggregationScoringService;
    private AggregationRecordsCreator featureAggregationsCreator;
    private AggregatedDataStore scoredFeatureAggregatedStore;
    private int pageSize;
    private int maxGroupSize;
    private StoreManager storeManager;

    public FeatureAggregationsExecutionServiceImpl(BucketConfigurationService bucketConfigurationService,
                                                   EnrichedDataStore enrichedDataStore,
                                                   InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
                                                   FeatureAggregationScoringService featureAggregationScoringService,
                                                   AggregationRecordsCreator featureAggregationsCreator,
                                                   AggregatedDataStore scoredFeatureAggregatedStore,
                                                   StoreManager storeManager, int pageSize, int maxGroupSize,
                                                   MetricContainerFlusher metricContainerFlusher) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureAggregationScoringService = featureAggregationScoringService;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.featureAggregationsCreator = featureAggregationsCreator;
        this.scoredFeatureAggregatedStore = scoredFeatureAggregatedStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.storeManager = storeManager;
        this.metricContainerFlusher = metricContainerFlusher;
    }

    //todo: data source should be event_type
    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        FeatureAggregationService featureAggregationBucketsService = new FeatureAggregationService(fixedDurationStrategy, bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureAggregationScoringService, featureAggregationsCreator, scoredFeatureAggregatedStore, pageSize, maxGroupSize, metricContainerFlusher);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        featureAggregationBucketsService.execute(timeRange, schema.getName());
        storeManager.cleanupCollections(startDate);
    }

    @Override
    public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        storeManager.cleanupCollections(startDate, endDate);
    }

    @Override
    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        // TODO: Implement
    }
}

