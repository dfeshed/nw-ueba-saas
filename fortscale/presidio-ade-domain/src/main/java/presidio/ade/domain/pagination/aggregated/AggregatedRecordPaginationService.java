package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregatedRecordPaginationService extends PaginationServiceBySet<AdeAggregationRecord> {
    private static final Logger logger = Logger.getLogger(AggregatedRecordPaginationService.class);

    private AggregatedDataReader dataReader;
    public AggregatedRecordPaginationService(int amountOfContextsInPage,AggregatedDataReader dataReader) {
        super(amountOfContextsInPage, amountOfContextsInPage);
        this.dataReader = dataReader;
    }

    @Override
    protected List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange) {
        Set<String> distinctContextIds = dataReader.findDistinctContextIds(timeRange, aggregatedDataPaginationParamSet);
        logger.debug("found amount of {} distinct contextIds",distinctContextIds.size());
        List<ContextIdToNumOfItems> contextIdToNumOfItems = distinctContextIds.stream().map(contextId -> new ContextIdToNumOfItems(contextId, 1)).collect(Collectors.toList());

        return contextIdToNumOfItems;
    }

    @Override
    protected <U extends AdeAggregationRecord> PageIterator<U> createPageIterator(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        return new AggregatedRecordsPageIterator<>(dataReader,aggregatedDataPaginationParamSet,contextIds,timeRange);
    }

    @Override
    /**
     * index provided at {@link presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord}
     */
    protected void ensureContextAndDateTimeIndex(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet) {
    }
}
