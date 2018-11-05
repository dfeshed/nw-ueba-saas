package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.store.record.StoreMetadataProperties;
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

    private static final String SCORED_ENRICHED_ADE_EVENT_TYPE_PREFIX_FORMAT = AdeScoredEnrichedRecord.EVENT_TYPE_PREFIX + ".%s";

    private final AggregatedDataStore aggregatedDataStore;
    private final FeatureBucketService<AdeScoredEnrichedRecord> featureBucketService;
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
                                    FeatureBucketService<AdeScoredEnrichedRecord> featureBucketService,
                                    AggregationRecordsCreator aggregationRecordsCreator,
                                    AggregatedDataStore aggregatedDataStore,
                                    AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
                                    int pageSize, int maxGroupSize, MetricContainerFlusher metricContainerFlusher) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.featureBucketService = featureBucketService;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.aggregatedDataStore = aggregatedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.metricsContainer = metricContainerFlusher;
    }


    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource, String contextType, List<String> contextFieldNamesToExclude, StoreMetadataProperties storeMetadataProperties) {

        //Once modelCacheManager save model to cache it will never updating the cache again with newer model.
        //Reset cache required in order to get newer models each partition and not use older models.
        // If this line will be deleted the model cache will need to have some efficient refresh mechanism.
        enrichedEventsScoringService.resetModelCache();

        boolean isStoreScoredEnrichedRecords = isStoreScoredEnrichedRecords(timeRange, dataSource);
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, pageSize, maxGroupSize, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
        FeatureBucketStrategyData featureBucketStrategyData = createFeatureBucketStrategyData(timeRange);

        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            while (pageIterator.hasNext()) {
                List<EnrichedRecord> pageRecords = pageIterator.next();
                List<AdeScoredEnrichedRecord> adeScoredRecords = enrichedEventsScoringService.scoreAndStoreEvents(pageRecords, isStoreScoredEnrichedRecords,timeRange, storeMetadataProperties);
                featureBucketService.updateFeatureBuckets(adeScoredRecords, contextType,
                        contextFieldNamesToExclude, featureBucketStrategyData);
            }

            List<FeatureBucket> closedBuckets = featureBucketService.closeFeatureBuckets();
            List<AdeAggregationRecord> aggrRecords = aggregationRecordsCreator.createAggregationRecords(closedBuckets);
            aggregatedDataStore.store(aggrRecords, AggregatedFeatureType.SCORE_AGGREGATION, storeMetadataProperties);
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

    @Override
    protected List<List<String>> getListsOfContextFieldNames(String dataSource, FixedDurationStrategy strategy) {
        String adeEventTypePrefix = String.format(SCORED_ENRICHED_ADE_EVENT_TYPE_PREFIX_FORMAT, dataSource);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        List<FeatureBucketConf> featureBucketConfs = aggregatedFeatureEventConfs.stream().map(x -> x.getBucketConf()).collect(Collectors.toList());
        featureBucketConfs = featureBucketConfs.stream().filter(bucketConf -> bucketConf.getAdeEventTypes().get(0).startsWith(adeEventTypePrefix)).collect(Collectors.toList());
        return featureBucketConfs.stream().map(featureBucketConf -> featureBucketConf.getContextFieldNames()).collect(Collectors.toList());
    }
}
