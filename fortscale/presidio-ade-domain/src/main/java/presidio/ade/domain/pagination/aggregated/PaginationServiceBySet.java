package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.BasePaginationService;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.pagination.PaginationService;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PaginationServiceBySet<T> extends BasePaginationService<T> {


    private static final Logger logger = Logger.getLogger(PaginationService.class);

    public PaginationServiceBySet(int pageSize, int maxGroupSize) {
        super(pageSize,maxGroupSize);
    }

    /**
     * Create pageIterators:
     * Get map of contextId and number of events for each contextId.
     * Get groups by the map, the group list contains pairs of events amount and contextIds.
     * Create pageIterator for each group.
     *
     * @param aggregatedDataPaginationParamSet ade event type
     * @param timeRange  the time range
     * @return list of PageIterators
     */
    public <U extends T> List<PageIterator<U>> getPageIterators(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange) {

        //Validate if indexes exist, otherwise add them.
        ensureContextAndDateTimeIndex(aggregatedDataPaginationParamSet);

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = getContextIdToNumOfItemsList(aggregatedDataPaginationParamSet, timeRange);
        //groups is a list, where each group contains pair of total num of events and set of contextId.
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsList);
        List<PageIterator<U>> pageIteratorList = new ArrayList<>(groups.size());

        //create pageIterator of each group
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            int totalNumOfItems = group.getKey();
            PageIterator<U> pageIterator = createPageIterator(aggregatedDataPaginationParamSet, timeRange, contextIds, totalNumOfItems);
            pageIteratorList.add(pageIterator);
        }

        logger.debug("Num of page iterators is: {}",pageIteratorList.size());
        return pageIteratorList;
    }

    /**
     * Create map of context ids and num of events based on timeRange and adeEventType.
     *
     * @param aggregatedDataPaginationParamSet ade event type
     * @param timeRange  the time range
     * @return map of context id and total num of events
     */
    protected abstract List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange);

    /**
     * Create pageIterator
     *
     * @param aggregatedDataPaginationParamSet ade event type
     * @param timeRange the time range
     * @param contextIds set of context ids
     * @param totalNumOfItems num of events in PageIterator
     * @return PageIterator
     */
    protected abstract <U extends T> PageIterator<U> createPageIterator(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems);

    /**
     * Validate the store indexes.
     * The implementations should validate that the fields they query should be indexed in their store.
     * @param aggregatedDataPaginationParamSet
     */
    protected abstract void ensureContextAndDateTimeIndex(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet);
}
