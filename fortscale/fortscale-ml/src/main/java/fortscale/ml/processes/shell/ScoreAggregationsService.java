package fortscale.ml.processes.shell;

import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1. Iterates over enriched records and scores them
 * 2. Insert the scored events into buckets
 * 3. create scored aggregations
 *
 * Created by barak_schuster on 6/11/17.
 */
public class ScoreAggregationsService extends FixedDurationStrategyExecutor {

    private Map<String/*contextType*/, EnrichedRecordPaginationService> contextTypeToEnrichedRecordPaginationServiceMap;
    private List<String/*contextType*/> contextTypes;
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    private ScoreAggregationsCreator scoreAggregationsCreator;
    private EnrichedDataStore enrichedDataStore;
    private EnrichedEventsScoringService enrichedEventsScoringService;
    /**
     * C'tor
     *
     * @param strategy
     * @param enrichedDataStore
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        fillContextTypeToPaginationServiceMap();
    }

    private void fillContextTypeToPaginationServiceMap() {
        contextTypes = Collections.singletonList("normalized_username");
        contextTypeToEnrichedRecordPaginationServiceMap = new HashMap<>();
        for (String contextType : contextTypes) {
            int numberOfEventIds = 1000;
            int numberOfContextIds = 100;
            EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, numberOfEventIds, numberOfContextIds, contextType);
            contextTypeToEnrichedRecordPaginationServiceMap.put(contextType, enrichedRecordPaginationService);
        }
    }

    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource) {

        for (String contextType : contextTypes) {
            EnrichedRecordPaginationService enrichedRecordPaginationService =
                    contextTypeToEnrichedRecordPaginationServiceMap.get(contextType);
            List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
            for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
                while (pageIterator.hasNext()) {
                    List<EnrichedRecord> pageRecords = pageIterator.next();
                    List<AdeScoredRecord> adeScoredRecords = enrichedEventsScoringService.scoreAndStoreEvents(pageRecords);
                    scoreAggregationsBucketService.updateBuckets(adeScoredRecords);
                }
                List<Object> closedBuckets = scoreAggregationsBucketService.closeBuckets();
                scoreAggregationsCreator.createScoreAggregations(closedBuckets);
            }
        }
    }
}
