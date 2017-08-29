package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.BasePaginationService;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PaginationServiceBySet<T> extends BasePaginationService<T> {
    private static final Logger logger = Logger.getLogger(PaginationServiceBySet.class);

    public PaginationServiceBySet(int pageSize, int maxGroupSize) {
        super(pageSize, maxGroupSize);
    }

    /**
     * Create page iterators:
     * Get a map from context ID to number of items.
     * Get groups from this map, each group is a pair of "total number of items" and "context IDs".
     * Create a page iterator for each group.
     *
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param timeRange                        the time range
     * @return list of page iterators
     */
    public <U extends T> List<PageIterator<U>> getPageIterators(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange) {
        return doGetPageIterators(aggregatedDataPaginationParamSet, timeRange, null);
    }

    /**
     * Create page iterators:
     * Get a map from context ID to number of items.
     * Get groups from this map, each group is a pair of "total number of items" and "context IDs".
     * Create a page iterator for each group.
     *
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     * @param timeRange                        the time range
     * @param threshold                        only items with a value / score larger than this threshold will be included
     * @return list of page iterators
     */
    public <U extends T> List<PageIterator<U>> getPageIterators(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Double threshold) {
        return doGetPageIterators(aggregatedDataPaginationParamSet, timeRange, threshold);
    }

    private <U extends T> List<PageIterator<U>> doGetPageIterators(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Double threshold) {
        // Validate if indexes exist, otherwise add them.
        ensureContextAndDateTimeIndex(aggregatedDataPaginationParamSet);
        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = getContextIdToNumOfItemsList(aggregatedDataPaginationParamSet, timeRange, threshold);
        // Groups is a list, where each group contains a pair of "total number of items" and set of "context IDs".
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsList);
        List<PageIterator<U>> pageIteratorList = new ArrayList<>(groups.size());

        // Create a page iterator for each group.
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            PageIterator<U> pageIterator = createPageIterator(aggregatedDataPaginationParamSet, timeRange, contextIds, threshold);
            pageIteratorList.add(pageIterator);
        }

        logger.debug("Num of page iterators is: {}", pageIteratorList.size());
        return pageIteratorList;
    }

    /**
     * Create a map from context ID to num of items based on adeEventType, timeRange and threshold
     *
     * @param aggregatedDataPaginationParamSet ade event type
     * @param timeRange                        the time range
     * @param threshold                        threshold for value / score (larger than)
     * @return map of context ID and total num of items
     */
    protected abstract List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Double threshold);

    /**
     * Create a page iterator
     *
     * @param aggregatedDataPaginationParamSet ade event type
     * @param timeRange                        the time range
     * @param contextIds                       set of context IDs
     * @param threshold                        threshold for value / score (larger than)
     * @return PageIterator
     */
    protected abstract <U extends T> PageIterator<U> createPageIterator(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Set<String> contextIds, Double threshold);

    /**
     * Validate the store indexes.
     * The implementations should validate that the fields they query should be indexed in their store.
     *
     * @param aggregatedDataPaginationParamSet contains list of features and their type
     */
    protected abstract void ensureContextAndDateTimeIndex(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet);
}
