package fortscale.utils.pagination;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import fortscale.utils.data.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * PaginationService responsible for:
 * Create map of context id to num of events
 * Create groups of context ids based on the map, pageSize(num of events ids in page) and maxGroupSize(num of context ids in group).
 * Create PageIterator for each group, while each PageIterator should be consist of pages.
 * Use getPageIterators() method to get list of PageIterators.
 * <p>
 * See reference for test: EnrichedRecordPaginationServiceTest, PaginationServiceTest
 *
 * @param <T>
 */
public abstract class PaginationService<T> extends BasePaginationService<T> {


    private String sortBy;

    private static final Logger logger = Logger.getLogger(PaginationService.class);

    public PaginationService(int pageSize, int maxGroupSize) {
        this(pageSize, maxGroupSize, null);
    }

    public PaginationService(int pageSize, int maxGroupSize, String sortBy) {
        super(pageSize,maxGroupSize);
        this.sortBy = sortBy;
    }

    /**
     * Create pageIterators:
     * Get map of contextId and number of events for each contextId.
     * Get groups by the map, the group list contains pairs of events amount and contextIds.
     * Create pageIterator for each group.
     *
     * @param adeEventType ade event type
     * @param timeRange  the time range
     * @return list of PageIterators
     */
    public <U extends T> List<PageIterator<U>> getPageIterators(String adeEventType, TimeRange timeRange) {

        //Validate if indexes exist, otherwise add them.
        ensureContextAndDateTimeIndex(adeEventType);

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = getContextIdToNumOfItemsList(adeEventType, timeRange);
        //groups is a list, where each group contains pair of total num of events and set of contextId.
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsList);
        List<PageIterator<U>> pageIteratorList = new ArrayList<>(groups.size());

        //create pageIterator of each group
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            int totalNumOfItems = group.getKey();
            PageIterator<U> pageIterator = createPageIterator(adeEventType, timeRange, contextIds, totalNumOfItems);
            pageIteratorList.add(pageIterator);
        }

        logger.debug("Num of page iterators is: {}",pageIteratorList.size());
        return pageIteratorList;
    }

    public PageIterator<T> getPageIterator(String adeEventType, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        PageIterator<T> pageIterator = createPageIterator(adeEventType, timeRange, contextIds, totalNumOfItems);
        return pageIterator;
    }

    /**
     * Create map of context ids and num of events based on timeRange and adeEventType.
     *
     * @param adeEventType ade event type
     * @param timeRange  the time range
     * @return map of context id and total num of events
     */
    protected abstract List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String adeEventType, TimeRange timeRange);

    /**
     * Create pageIterator
     *
     * @param adeEventType ade event type
     * @param timeRange the time range
     * @param contextIds set of context ids
     * @param totalNumOfItems num of events in PageIterator
     * @return PageIterator
     */
    protected abstract <U extends T> PageIterator<U> createPageIterator(String adeEventType, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems);

    /**
     * Validate the store indexes.
     * The implementations should validate that the fields they query should be indexed in their store.
     */
    protected abstract void ensureContextAndDateTimeIndex(String adeEventType);




    /**
     * @return get field name that paginationService uses to sort pages
     */
    public String getSortBy() {
        return sortBy;
    }
}


