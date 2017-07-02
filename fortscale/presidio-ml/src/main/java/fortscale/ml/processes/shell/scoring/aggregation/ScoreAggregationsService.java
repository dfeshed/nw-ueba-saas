package fortscale.ml.processes.shell.scoring.aggregation;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsCreator;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.*;

/**
 * 1. Iterates over enriched records and scores them
 * 2. Insert the scored events into buckets
 * 3. create scored aggregations
 *
 * Created by barak_schuster on 6/11/17.
 */
public class ScoreAggregationsService extends FixedDurationStrategyExecutor {

    private EnrichedRecordPaginationService enrichedRecordPaginationService;
    private String contextType;
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    private ScoreAggregationsCreator scoreAggregationsCreator;
    private EnrichedDataStore enrichedDataStore;
    private EnrichedEventsScoringService enrichedEventsScoringService;
    /**
     * C'tor
     *  @param strategy
     * @param enrichedDataStore
     * @param enrichedEventsScoringService
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore,
                                    EnrichedEventsScoringService enrichedEventsScoringService, ScoreAggregationsBucketService scoreAggregationsBucketService) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.scoreAggregationsBucketService = scoreAggregationsBucketService;
        this.contextType = "normalized_username";
        enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, contextType);
    }


    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = new ArrayList<>();
        contextTypes.add(contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            while (pageIterator.hasNext()) {
                List<EnrichedRecord> pageRecords = pageIterator.next();
                List<AdeScoredEnrichedRecord> adeScoredRecords = enrichedEventsScoringService.scoreAndStoreEvents(pageRecords);
                scoreAggregationsBucketService.updateBuckets(adeScoredRecords,contextTypes,createFeatureBucketStrategyData(timeRange));
            }
            List<FeatureBucket> closedBuckets = scoreAggregationsBucketService.closeBuckets();
//                scoreAggregationsCreator.createScoreAggregations(closedBuckets);
        }
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange){
        String strategyName = StringUtils.lowerCase(this.strategy.name());
        return new FeatureBucketStrategyData(strategyName,strategyName,timeRange.getStart().getEpochSecond(), timeRange.getEnd().getEpochSecond());
    }
}
