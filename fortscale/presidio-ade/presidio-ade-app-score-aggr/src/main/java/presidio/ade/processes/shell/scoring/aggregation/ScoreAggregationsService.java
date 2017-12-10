package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 1. Iterates over enriched records and scores them
 * 2. Insert the scored events into buckets
 * 3. create scored aggregations
 *
 * Created by barak_schuster on 6/11/17.
 */
public class ScoreAggregationsService extends FixedDurationStrategyExecutor {

    private final AggregatedDataStore aggregatedDataStore;
    private final ScoreAggregationsBucketService scoreAggregationsBucketService;
    private final AggregationRecordsCreator aggregationRecordsCreator;
    private final EnrichedDataStore enrichedDataStore;
    private final EnrichedEventsScoringService enrichedEventsScoringService;
    private final MetricContainerFlusher metricsContainer;
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    private Map<String, Set<TimeRange>> storedDataSourceToTimeRanges = new HashMap<>();
    private int pageSize;
    private int maxGroupSize;

    /**
     * C'tor
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore,
                                    EnrichedEventsScoringService enrichedEventsScoringService,
                                    ScoreAggregationsBucketService scoreAggregationsBucketService,
                                    AggregationRecordsCreator aggregationRecordsCreator,
                                    AggregatedDataStore aggregatedDataStore,
                                    AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
                                    int pageSize, int maxGroupSize, MetricContainerFlusher metricContainerFlusher) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.scoreAggregationsBucketService = scoreAggregationsBucketService;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.aggregatedDataStore = aggregatedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.metricsContainer = metricContainerFlusher;
    }


    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource, String contextType) {

        //Once modelCacheManager save model to cache it will never updating the cache again with newer model.
        //Reset cache required in order to get newer models each partition and not use older models.
        // If this line will be deleted the model cache will need to have some efficient refresh mechanism.
        enrichedEventsScoringService.resetModelCache();

        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = new ArrayList<>();
        contextTypes.add(contextType);
        boolean isStoreScoredEnrichedRecords = isStoreScoredEnrichedRecords(timeRange, dataSource);
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, pageSize, maxGroupSize, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
        FeatureBucketStrategyData featureBucketStrategyData = createFeatureBucketStrategyData(timeRange);

        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            while (pageIterator.hasNext()) {
                List<EnrichedRecord> pageRecords = pageIterator.next();
                List<AdeScoredEnrichedRecord> adeScoredRecords = enrichedEventsScoringService.scoreAndStoreEvents(pageRecords, isStoreScoredEnrichedRecords,timeRange);
                scoreAggregationsBucketService.updateBuckets(adeScoredRecords, contextTypes, featureBucketStrategyData);
            }

            List<FeatureBucket> closedBuckets = scoreAggregationsBucketService.closeBuckets();
            List<AdeAggregationRecord> aggrRecords = aggregationRecordsCreator.createAggregationRecords(closedBuckets);
            aggregatedDataStore.store(aggrRecords, AggregatedFeatureType.SCORE_AGGREGATION);
        }

        //Flush stored metrics to elasticsearch
        metricsContainer.flush();
    }

    private boolean isStoreScoredEnrichedRecords(TimeRange timeRange, String dataSource){
        boolean ret = false;
        Set<TimeRange> storedTimeRangeSet = storedDataSourceToTimeRanges.get(dataSource);
        if(storedTimeRangeSet == null){
            storedTimeRangeSet = new HashSet<>();
            storedDataSourceToTimeRanges.put(dataSource, storedTimeRangeSet);
        }

        if(!storedTimeRangeSet.contains(timeRange)){
            storedTimeRangeSet.add(timeRange);
            ret = true;
        }

        return ret;
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange) {
        String strategyName = strategy.toStrategyName();
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
    }

    public List<String> getDistinctContextTypes(String dataSource){
        //todo: fix this implementation.
        //this implementation returns the distinct context over all data sources which might cause us to run on context which not exist for the specific data source.
        // we should not fail in this case just work for nothing.
        List<String> ret = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().map(x -> x.getBucketConf().getContextFieldNames()).flatMap(List::stream).distinct().collect(Collectors.toList());
//        String confSuffix = dataSource+strategy;
//        List<String> ret = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().filter(x ->  StringUtils.endsWithIgnoreCase(x.getName(),confSuffix)).map(x -> x.getBucketConf().getContextFieldNames()).flatMap(List::stream).distinct().collect(Collectors.toList());
        return ret;
    }
}
