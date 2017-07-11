package fortscale.utils.pagination;

import fortscale.utils.time.TimeRange;
import javafx.util.Pair;

import java.util.*;
import fortscale.utils.logging.Logger;

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
public abstract class PaginationService<T> {

    // num of events in page
    private int pageSize;
    // num of context ids in group
    private int maxGroupSize;

    private static final Logger logger = Logger.getLogger(PaginationService.class);

    public PaginationService(int pageSize, int maxGroupSize) {
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * Create pageIterators:
     * Get map of contextId and number of events for each contextId.
     * Get groups by the map, the group list contains pairs of events amount and contextIds.
     * Create pageIterator for each group.
     *
     * @param adeEventType data source name
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

    /**
     * Create map of context ids and num of events based on timeRange and adeEventType.
     *
     * @param adeEventType data source name
     * @param timeRange  the time range
     * @return map of context id and total num of events
     */
    protected abstract List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String adeEventType, TimeRange timeRange);

    /**
     * Create pageIterator
     *
     * @param adeEventType data source name
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
     *
     * Creates groups, which contain pair of total num of events and set of contextIds.
     *
     * In order to minimize the number of context ids in each group and to minimize the number of groups:
     * pop out context id with the largest amount of events and pop out context ids with smallest amount of events.
     *
     * @param contextIdToNumOfItemsList list of ContextIdToNumOfItems objects,each object contains context id and num of events
     * @return list num of events in group and set of contextId of pairs
     *
     *Examples:
     * pageSize=3, maxGroupSize=2
     * case 1:
     *  contextIdToNumOfItemsList: a->5 events, b-> 2 events, c-> 2 events
     *  the groups should be {a},{b},{c}
     * case 2:
     *  contextIdToNumOfItemsList: a->5 events, b-> 2 events, c-> 1 events
     *  the groups should be {a},{b,c}
     */
    private List<Pair<Integer, Set<String>>> getGroups(List<ContextIdToNumOfItems> contextIdToNumOfItemsList) {

        List<ContextIdToNumOfItems> sortedList = new ArrayList<>(contextIdToNumOfItemsList);
        sortedList.sort(Comparator.comparing(c -> c.getTotalNumOfItems()));

        // Integer - total num of events in group
        // Set<String> - contextIds
        List<Pair<Integer, Set<String>>> groups = new ArrayList<>();

        int totalNumOfItems = 0;
        int start = 0;
        int end = sortedList.size() - 1;
        int numOfHandledContextIds = 0;

        // Next condition handle 2 cases: (end == start && numOfHandledContextIds == contextIdToNumOfItemsList.size()-1)
        // # first case: contextIdToNumOfItemsList contains only one item.
        // # second case: all context ids were handled and inserted to groups except the last context id:
        // It may happened, where start = end -1 and the last context id can not join to current group due to pageSize or maxGroupSize.
        // additional group should be created for last context id. (See case 2 in examples above).
        while (end > start || (end == start && numOfHandledContextIds == sortedList.size()-1)) {
            Set<String> contextIds = new HashSet<>();
            ContextIdToNumOfItems first = sortedList.get(start);
            ContextIdToNumOfItems last = sortedList.get(end);
            contextIds.add(last.getContextId());
            totalNumOfItems = last.getTotalNumOfItems();
            numOfHandledContextIds++;

            while (totalNumOfItems + first.getTotalNumOfItems() <= pageSize && contextIds.size() + 1 <= maxGroupSize &&
                    end > start) {
                totalNumOfItems += first.getTotalNumOfItems();
                contextIds.add(first.getContextId());
                start++;
                first = contextIdToNumOfItemsList.get(start);
                numOfHandledContextIds++;
            }

            Pair<Integer, Set<String>> totalNumOfItemsToContextIdsPair = new Pair<>(totalNumOfItems, contextIds);
            groups.add(totalNumOfItemsToContextIdsPair);
            end--;
        }
        return groups;
    }
}


