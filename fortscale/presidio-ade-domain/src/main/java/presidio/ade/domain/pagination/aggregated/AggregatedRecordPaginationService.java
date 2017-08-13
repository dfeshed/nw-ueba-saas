package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AggregatedRecordPaginationService responsible for:
 * Create map of active contextid's across aggregated features in a given timerange
 * Create groups of context ids based on the map, pageSize(number of contexts in page)
 * Create PageIterator for each group, while each PageIterator should be consist of one page.
 * - all the data of set contexts is in one page. assuming that aggregated data will not be consume a lot of memory resources
 *
 * Use getPageIterators() method to get list of PageIterators.
 * <p>
 * See reference for test: AggregatedRecordPaginationServiceTest
 *
 */
public class AggregatedRecordPaginationService extends PaginationServiceBySet<AdeAggregationRecord> {
    private static final Logger logger = Logger.getLogger(AggregatedRecordPaginationService.class);

    private AggregatedDataReader dataReader;
    public AggregatedRecordPaginationService(int amountOfContextsInPage,AggregatedDataReader dataReader) {
        super(amountOfContextsInPage, amountOfContextsInPage);
        this.dataReader = dataReader;
        this.dataReader.setAggregatedRecordPaginationService(this);
    }

    /**
     *
     * @param aggregatedDataPaginationParamSet ade event type
     * @param timeRange  the time range
     * @return map of active contextid's across aggregated features in a given time range
     */
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
