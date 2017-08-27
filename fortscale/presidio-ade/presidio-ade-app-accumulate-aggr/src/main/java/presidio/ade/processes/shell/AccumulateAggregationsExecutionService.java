package presidio.ade.processes.shell;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.common.general.Schema;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketService;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsService;

import java.time.Instant;

/**
 * Created by maria_dorohin on 7/26/17.
 */
public class AccumulateAggregationsExecutionService {
    private static Logger logger = LoggerFactory.getLogger(AccumulateAggregationsExecutionService.class);

    private final BucketConfigurationService bucketConfigurationService;
    private final EnrichedDataStore enrichedDataStore;
    private final AggregationEventsAccumulationDataStore aggregationEventsAccumulationDataStore;
    private final int pageSize;
    private final int maxGroupSize;
    private final AccumulateAggregationsBucketService accumulateAggregationsBucketService;
    private final AccumulationsCache accumulationsCache;

    public AccumulateAggregationsExecutionService(BucketConfigurationService bucketConfigurationService,
                                                  EnrichedDataStore enrichedDataStore,
                                                  AggregationEventsAccumulationDataStore aggregationEventsAccumulationDataStore,
                                                  AccumulateAggregationsBucketService accumulateAggregationsBucketService,
                                                  AccumulationsCache accumulationsCache, int pageSize, int maxGroupSize) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.aggregationEventsAccumulationDataStore = aggregationEventsAccumulationDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.accumulateAggregationsBucketService = accumulateAggregationsBucketService;
        this.accumulationsCache = accumulationsCache;
    }

    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration, Double featureBucketStrategy) throws Exception {
        //strategy for accumulator
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        //strategy for aggregator
        FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(featureBucketStrategy.longValue());
        AccumulateAggregationsService featureAggregationBucketsService = new AccumulateAggregationsService(fixedDurationStrategy, bucketConfigurationService, enrichedDataStore, aggregationEventsAccumulationDataStore, pageSize, maxGroupSize, strategy, accumulateAggregationsBucketService, accumulationsCache);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        featureAggregationBucketsService.execute(timeRange, schema.getName());
    }

    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }
}

