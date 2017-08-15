package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * C'tor
     * @param strategy
     * @param enrichedDataStore
     * @param enrichedEventsScoringService
     * @param aggregationRecordsCreator
     * @param aggregatedDataStore
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore,
                                    EnrichedEventsScoringService enrichedEventsScoringService,
                                    ScoreAggregationsBucketService scoreAggregationsBucketService,
                                    AggregationRecordsCreator aggregationRecordsCreator, AggregatedDataStore aggregatedDataStore) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.scoreAggregationsBucketService = scoreAggregationsBucketService;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.aggregatedDataStore = aggregatedDataStore;
    }


    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource, String contextType) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = new ArrayList<>();
        contextTypes.add(contextType);
        List<String> aggregationContext = getAggregationContext();

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
    }

    private List<String> getAggregationContext() {
        // todo: figure out from conf
        return Collections.singletonList("context.userId");
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange){
        String strategyName = strategy.toStrategyName();

        return new FeatureBucketStrategyData(strategyName,strategyName,timeRange);
    }

    public List<String> getDistinctContextTypes(String dataSource){
        //todo: figure it out from the configuration.
        return Collections.singletonList("userId");
    }
}
