package fortscale.ml.processes.shell.scoring.aggregation;

import fortscale.aggregation.creator.AggregationsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggrRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.aggr.AggrDataStore;
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

    private final AggrDataStore aggrDataStore;
    private final ScoreAggregationsBucketService scoreAggregationsBucketService;
    private final AggregationsCreator aggregationsCreator;
    private final EnrichedDataStore enrichedDataStore;
    private final EnrichedEventsScoringService enrichedEventsScoringService;

    /**
     * C'tor
     * @param strategy
     * @param enrichedDataStore
     * @param enrichedEventsScoringService
     * @param aggregationsCreator
     * @param aggrDataStore
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore,
                                    EnrichedEventsScoringService enrichedEventsScoringService, ScoreAggregationsBucketService scoreAggregationsBucketService, AggregationsCreator aggregationsCreator, AggrDataStore aggrDataStore) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.scoreAggregationsBucketService = scoreAggregationsBucketService;
        this.aggregationsCreator = aggregationsCreator;
        this.aggrDataStore = aggrDataStore;
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
                scoreAggregationsBucketService.updateBuckets(adeScoredRecords,contextTypes,createFeatureBucketStrategyData(timeRange));
            }
            List<FeatureBucket> closedBuckets = scoreAggregationsBucketService.closeBuckets();
            List<AdeAggrRecord> aggrRecords = aggregationsCreator.createAggregations(closedBuckets);
            aggrDataStore.store(aggrRecords);
        }
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange){
        String strategyName = StringUtils.lowerCase(this.strategy.name());
        return new FeatureBucketStrategyData(strategyName,strategyName,timeRange.getStart().getEpochSecond(), timeRange.getEnd().getEpochSecond());
    }

    public List<String> getDistinctContextTypes(String dataSource){
        //todo: figure it out from the configuration.
        return Collections.singletonList("normalized_username");
    }
}
