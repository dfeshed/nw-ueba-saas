package fortscale.ml.processes.shell;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 6/11/17.
 */
public class ScoreAggregationsService extends FixedDurationStrategyExecutor {

    private Map<String/*contextType*/, EnrichedRecordPaginationService> contextTypeToEnrichedRecordPaginationServiceMap;
    private List<String/*contextType*/> contextTypes;
    private RawEventsScoringService rawEventsScoringService;
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    private ScoreAggregationsCreator scoreAggregationsCreator;
    private EnrichedDataStore enrichedDataStore;

    /**
     * C'tor
     *
     * @param strategy
     * @param enrichedDataStore
     */
    public ScoreAggregationsService(FixedDurationStrategy strategy, EnrichedDataStore enrichedDataStore) {
        super(strategy);
        this.enrichedDataStore = enrichedDataStore;
        this.contextTypeToEnrichedRecordPaginationServiceMap = fillContextTypeToPaginationServiceMap();
    }

    private Map<String, EnrichedRecordPaginationService> fillContextTypeToPaginationServiceMap() {
        contextTypes = Collections.singletonList("normalized_username");
        Map<String/*contextType*/, EnrichedRecordPaginationService> result = new HashMap<>();
        for (String contextType : contextTypes) {
            int numberOfEventIds = 1000;
            int numberOfContextIds = 100;
            result.put(contextType, new EnrichedRecordPaginationService(enrichedDataStore, numberOfEventIds, numberOfContextIds, contextType));

        }
        return result;
    }

    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource) {

        for (String contextType : contextTypes) {
            EnrichedRecordPaginationService enrichedRecordPaginationService = contextTypeToEnrichedRecordPaginationServiceMap.get(contextType);
            List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
            for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
                while (pageIterator.hasNext()) {
                    List<EnrichedRecord> pageRecords = pageIterator.next();
                    rawEventsScoringService.scoreAndStore();
                    scoreAggregationsBucketService.updateBuckets();
                }
                scoreAggregationsBucketService.closeBuckets();
                scoreAggregationsCreator.createScoreAggregations();
            }

        }
    }
}
