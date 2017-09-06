package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private List<String> aggregationContext;
    private BucketConfigurationService bucketConfigurationService;
    private TtlService ttlService;

    /**
     * C'tor
     * @param strategy
     * @param enrichedDataStore
     * @param enrichedEventsScoringService
     * @param aggregationRecordsCreator
     * @param aggregatedDataStore
     * @param aggregatedFeatureEventsConfService
     * @param bucketConfigurationService
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore,
                                    EnrichedEventsScoringService enrichedEventsScoringService,
                                    ScoreAggregationsBucketService scoreAggregationsBucketService,
                                    AggregationRecordsCreator aggregationRecordsCreator, AggregatedDataStore aggregatedDataStore, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService, BucketConfigurationService bucketConfigurationService, TtlService ttlService) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.scoreAggregationsBucketService = scoreAggregationsBucketService;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.aggregatedDataStore = aggregatedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.bucketConfigurationService = bucketConfigurationService;
        this.ttlService = ttlService;
        this.aggregationContext = getAggregationContext();
    }


    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource, String contextType) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = new ArrayList<>();
        contextTypes.add(contextType);

        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            while (pageIterator.hasNext()) {
                List<EnrichedRecord> pageRecords = pageIterator.next();
                List<AdeScoredEnrichedRecord> adeScoredRecords = enrichedEventsScoringService.scoreAndStoreEvents(pageRecords);
                FeatureBucketStrategyData featureBucketStrategyData = createFeatureBucketStrategyData(timeRange);
                scoreAggregationsBucketService.updateBuckets(adeScoredRecords, aggregationContext, featureBucketStrategyData);
            }
            List<FeatureBucket> closedBuckets = scoreAggregationsBucketService.closeBuckets();
            List<AdeAggregationRecord> aggrRecords = aggregationRecordsCreator.createAggregationRecords(closedBuckets);
            aggregatedDataStore.store(aggrRecords, AggregatedFeatureType.SCORE_AGGREGATION);
        }
        ttlService.cleanupCollections(timeRange.getStart());
    }

    private List<String> getAggregationContext() {
        return aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().map(x -> x.getBucketConf().getContextFieldNames()).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange){
        String strategyName = strategy.toStrategyName();

        return new FeatureBucketStrategyData(strategyName,strategyName,timeRange);
    }

    public List<String> getDistinctContextTypes(String dataSource){
        Set<List<String>> distinctMultipleContextsTypeSet = bucketConfigurationService.getRelatedDistinctContexts(dataSource);
        Set<String> distinctSingleContextTypeSet = new HashSet<>();
        for (List<String> distinctMultipleContexts : distinctMultipleContextsTypeSet) {
            distinctSingleContextTypeSet.addAll(distinctMultipleContexts);
        }
        return new ArrayList<>(distinctSingleContextTypeSet);
    }
}
