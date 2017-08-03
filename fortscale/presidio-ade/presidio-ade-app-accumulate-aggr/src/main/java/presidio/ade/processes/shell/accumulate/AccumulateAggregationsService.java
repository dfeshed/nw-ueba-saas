package presidio.ade.processes.shell.accumulate;

import fortscale.accumulator.AccumulationsInMemory;
import fortscale.accumulator.AccumulationsStore;
import fortscale.accumulator.Accumulator;
import fortscale.accumulator.AccumulatorService;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.accumulator.AccumulatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.*;


public class AccumulateAggregationsService extends FixedDurationStrategyExecutor {

    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private AccumulatedDataStore accumulatedDataStore;
    private int pageSize;
    private int maxGroupSize;
    private FixedDurationStrategy featureBucketDuration;
    private AccumulateAggregationsBucketService accumulateAggregationsBucketService;
    private AccumulationsStore accumulationsStore;

    public AccumulateAggregationsService(FixedDurationStrategy fixedDurationStrategy,
                                         BucketConfigurationService bucketConfigurationService,
                                         EnrichedDataStore enrichedDataStore,
                                         AccumulatedDataStore accumulatedDataStore, int pageSize, int maxGroupSize, FixedDurationStrategy featureBucketDuration,
                                         AccumulateAggregationsBucketService accumulateAggregationsBucketService,
                                         AccumulationsStore accumulationsStore) {
        super(fixedDurationStrategy);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.accumulatedDataStore = accumulatedDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.featureBucketDuration = featureBucketDuration;
        this.accumulateAggregationsBucketService = accumulateAggregationsBucketService;
        this.accumulationsStore = accumulationsStore;
    }

    @Override
    protected void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = Collections.singletonList(contextType);

        //PaginationService sort pages by START_INSTANT_FIELD
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, pageSize, maxGroupSize, contextType, AdeRecord.START_INSTANT_FIELD);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(adeEventType, timeRange);

        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            Accumulator accumulatorService = new AccumulatorService(accumulationsStore, strategy);

            accumulateAggregationsBucketService.aggregateAndAccumulate(pageIterator, contextTypes, featureBucketDuration, accumulatorService);

            //get all accumulated records and clean the store
            List<AccumulatedAggregationFeatureRecord> accumulatedRecords = accumulationsStore.getAllAccumulatedRecords();
            accumulationsStore.clean();

            accumulatedDataStore.store(accumulatedRecords);
        }
    }


    @Override
    protected List<String> getDistinctContextTypes(String adeEventType) {
        Set<List<String>> distinctMultipleContextsTypeSet = bucketConfigurationService.getRelatedDistinctContexts(adeEventType);
        Set<String> distinctSingleContextTypeSet = new HashSet<>();
        for (List<String> distinctMultipleContexts : distinctMultipleContextsTypeSet) {
            distinctSingleContextTypeSet.addAll(distinctMultipleContexts);
        }
        return new ArrayList<>(distinctSingleContextTypeSet);
    }

}
