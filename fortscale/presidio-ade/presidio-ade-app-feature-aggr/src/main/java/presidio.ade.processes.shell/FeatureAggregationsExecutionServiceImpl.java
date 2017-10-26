package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.aggregation.FeatureAggregationService;

import java.time.Instant;

public class FeatureAggregationsExecutionServiceImpl implements PresidioExecutionService {
    private static Logger logger = LoggerFactory.getLogger(FeatureAggregationsExecutionServiceImpl.class);


    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private FeatureAggregationScoringService featureAggregationScoringService;
    private AggregationRecordsCreator featureAggregationsCreator;
    private AggregatedDataStore scoredFeatureAggregatedStore;
    private int pageSize;
    private int maxGroupSize;
    private TtlService ttlService;

    public FeatureAggregationsExecutionServiceImpl(BucketConfigurationService bucketConfigurationService,
                                                   EnrichedDataStore enrichedDataStore,
                                                   InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
                                                   FeatureAggregationScoringService featureAggregationScoringService,
                                                   AggregationRecordsCreator featureAggregationsCreator,
                                                   AggregatedDataStore scoredFeatureAggregatedStore,
                                                   TtlService ttlService, int pageSize, int maxGroupSize) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureAggregationScoringService = featureAggregationScoringService;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.featureAggregationsCreator = featureAggregationsCreator;
        this.scoredFeatureAggregatedStore = scoredFeatureAggregatedStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.ttlService = ttlService;
    }

    //todo: data source should be event_type
    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        FeatureAggregationService featureAggregationBucketsService = new FeatureAggregationService(fixedDurationStrategy, bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureAggregationScoringService, featureAggregationsCreator, scoredFeatureAggregatedStore, pageSize, maxGroupSize);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        featureAggregationBucketsService.execute(timeRange, schema.getName());
        ttlService.cleanupCollections(startDate);
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

