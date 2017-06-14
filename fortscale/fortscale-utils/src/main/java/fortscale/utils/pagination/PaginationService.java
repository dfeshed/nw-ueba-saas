package fortscale.utils.pagination;

import fortscale.utils.process.metrics.jvm.impl.JVMMetricsServiceImpl;
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
 * See reference for test: EnrichedRecordPaginationServiceTest, EnrichedRecordPaginationServiceTest
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
     * @param dataSource data source name
     * @param timeRange
     * @return list of PageIterators
     */
    public <U extends T> List<PageIterator<U>> getPageIterators(String dataSource, TimeRange timeRange) {

        //Validate if indexes exist, otherwise add them.
        validateIndexes(dataSource);

        List<ContextIdToNumOfEvents> contextIdToNumOfEventsList = getContextIdToNumOfItemsList(dataSource, timeRange);
        //groups is a list, where each group contains pair of total num of events and set of contextId.
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfEventsList);
        List<PageIterator<U>> pageIteratorList = new ArrayList<>(groups.size());

        //create pageIterator of each group
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            int totalNumOfItems = group.getKey();
            PageIterator<U> pageIterator = createPageIterator(dataSource, timeRange, contextIds, totalNumOfItems);
            pageIteratorList.add(pageIterator);
        }

        logger.debug("Num of page iterators is: {}",pageIteratorList.size());
        return pageIteratorList;
    }

    /**
     * Create map of context ids and num of events based on timeRange and dataSource.
     *
     * @param dataSource data source name
     * @param timeRange
     * @return map of context id and total num of events
     */
    protected abstract List<ContextIdToNumOfEvents> getContextIdToNumOfItemsList(String dataSource, TimeRange timeRange);

    /**
     * Create pageIterator
     *
     * @param dataSource data source name
     * @param timeRange
     * @param contextIds set of context ids
     * @param totalNumOfItems num of events in PageIterator
     * @return PageIterator
     */
    protected abstract <U extends T> PageIterator<U> createPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems);

    /**
     * Validate the store indexes.
     * The implementations should validate that the fields they query should be indexed in their store.
     */
    protected abstract void validateIndexes(String dataSource);


    /**
     * Creates groups, which contain pair of total num of events in group and set of contextId:
     * Sort and covert contextIdToNumOfItems map to  List<Pair<String, Integer>> of context ids to total num of events
     * Add the last contextId to set.
     * while num of events less than pageSize and contextIds set amount less than maxGroupSize => Add first contextIds to set.
     *
     * @param contextIdToNumOfEventsList list of ContextIdToNumOfEvents objects,each object contains context id and num of events
     * @return list num of events in group and set of contextId of pairs
     */
    private List<Pair<Integer, Set<String>>> getGroups(List<ContextIdToNumOfEvents> contextIdToNumOfEventsList) {
        contextIdToNumOfEventsList.sort(Comparator.comparing(c -> c.getTotalNumOfEvents()));

        // Integer - total num of events in group
        // Set<String> - contextIds
        List<Pair<Integer, Set<String>>> groups = new ArrayList<>();

        int totalNumOfItems = 0;
        int start = 0;
        int end = contextIdToNumOfEventsList.size() - 1;

        while (end > start) {
            Set<String> contextIds = new HashSet<>();
            ContextIdToNumOfEvents first = contextIdToNumOfEventsList.get(start);
            ContextIdToNumOfEvents last = contextIdToNumOfEventsList.get(end);
            contextIds.add(last.getContextId());
            totalNumOfItems = last.getTotalNumOfEvents();

            while (totalNumOfItems + first.getTotalNumOfEvents() <= pageSize && contextIds.size() + 1 <= maxGroupSize &&
                    end > start) {
                totalNumOfItems += first.getTotalNumOfEvents();
                contextIds.add(first.getContextId());
                start++;
                first = contextIdToNumOfEventsList.get(start);
            }

            Pair<Integer, Set<String>> totalNumOfItemsToContextIdsPair = new Pair<>(totalNumOfItems, contextIds);
            groups.add(totalNumOfItemsToContextIdsPair);
            end--;
        }
        return groups;
    }
}


