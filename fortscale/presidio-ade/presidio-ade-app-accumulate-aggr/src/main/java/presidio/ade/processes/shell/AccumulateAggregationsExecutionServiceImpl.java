package presidio.ade.processes.shell;

import fortscale.accumulator.AccumulationsStore;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.domain.store.accumulator.AccumulatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketService;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsService;

import java.time.Instant;

/**
 * Created by maria_dorohin on 7/26/17.
 */
public class AccumulateAggregationsExecutionServiceImpl implements PresidioExecutionService {
    private static Logger logger = LoggerFactory.getLogger(AccumulateAggregationsExecutionServiceImpl.class);


    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private AccumulatedDataStore accumulatedDataStore;
    private int pageSize;
    private int maxGroupSize;
    private AccumulateAggregationsBucketService accumulateAggregationsBucketService;
    private AccumulationsStore accumulationsStore;

    public AccumulateAggregationsExecutionServiceImpl(BucketConfigurationService bucketConfigurationService,
                                                      EnrichedDataStore enrichedDataStore,
                                                      AccumulatedDataStore accumulatedDataStore,
                                                      AccumulateAggregationsBucketService accumulateAggregationsBucketService,
                                                      AccumulationsStore accumulationsStore, int pageSize, int maxGroupSize) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.accumulatedDataStore = accumulatedDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.accumulateAggregationsBucketService = accumulateAggregationsBucketService;
        this.accumulationsStore = accumulationsStore;
    }

    @Override
    public void run(DataSource dataSource, Instant startDate, Instant endDate, Double fixedDuration, Double featureBucketStrategy) throws Exception {
        //strategy for accumulator
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDuration.longValue());
        //strategy for aggregator
        FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(featureBucketStrategy.longValue());
        AccumulateAggregationsService featureAggregationBucketsService = new AccumulateAggregationsService(fixedDurationStrategy, bucketConfigurationService, enrichedDataStore, accumulatedDataStore, pageSize, maxGroupSize, strategy, accumulateAggregationsBucketService, accumulationsStore);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        featureAggregationBucketsService.execute(timeRange, dataSource.getName());
    }

    @Override
    public void clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanAll(DataSource dataSource) throws Exception {
        // TODO: Implement
    }
}

