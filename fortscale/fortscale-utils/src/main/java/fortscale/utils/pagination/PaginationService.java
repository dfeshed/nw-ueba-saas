package fortscale.utils.pagination;

import fortscale.utils.time.TimeRange;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PaginationService responsible for:
 * Create map of context id to num of events
 * Create groups of context ids based on the map, pageSize(num of events ids in page) and maxGroupSize(num of context ids in group).
 * Create PageIterator for each group, while each PageIterator should be consist of pages.
 * Use getPageIterators() method to get list of PageIterators.
 * <p>
 * See reference for test: EnrichedRecordPaginationServiceTest
 *
 * @param <T>
 */
public abstract class PaginationService<T> {

    // num of events ids in page
    private int pageSize;
    // num of context ids in group
    private int maxGroupSize;

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
     * @param dataSource
     * @param timeRange
     * @return list of PageIterators
     */
    public <U extends T> List<PageIterator<U>> getPageIterators(String dataSource, TimeRange timeRange) {

        Map<String, Integer> contextIdToNumOfItemsMap = getContextIdToNumOfItemsMap(dataSource, timeRange);

        //groups is a list, where each group contains pair of total num of events and set of contextId.
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsMap);
        List<PageIterator<U>> pageIteratorList = new ArrayList<>(groups.size());

        //create pageIterator of each group
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            int totalNumOfItems = group.getKey();
            PageIterator<U> pageIterator = getPageIterator(dataSource, timeRange, contextIds, totalNumOfItems);
            pageIteratorList.add(pageIterator);
        }

        return pageIteratorList;
    }

    /**
     * Create map of context ids and num of events based on timeRange and dataSource.
     *
     * @param dataSource
     * @param timeRange
     * @return map of context id and total num of events
     */
    protected abstract Map<String, Integer> getContextIdToNumOfItemsMap(String dataSource, TimeRange timeRange);

    /**
     * Create pageIterator
     *
     * @param dataSource
     * @param timeRange
     * @param contextIds
     * @param totalNumOfItems num of events in PageIterator
     * @return PageIterator
     */
    protected abstract <U extends T> PageIterator<U> getPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems);

    /**
     * Validate the store indexes.
     * The implementations should validate that the fields they query should be indexed in their store.
     */
    protected abstract void validateIndexes();


    /**
     * Creates groups, which contain pair of total num of events in group and set of contextId:
     * Sort and covert contextIdToNumOfItems map to  List<Pair<String, Integer>> of context ids to total num of events
     * Add the last contextId to set.
     * while num of events less than pageSize and contextIds set amount less than maxGroupSize => Add first contextIds to set.
     *
     * @param contextIdToNumOfItemsMap map of context id to num of events
     * @return list num of events in group and set of contextId of pairs
     */
    private List<Pair<Integer, Set<String>>> getGroups(Map<String, Integer> contextIdToNumOfItemsMap) {

        // Pair<String, Integer> - pair of context ids to total num of events
        List<Pair<String, Integer>> contextIdToNumOfItemsList = contextIdToNumOfItemsMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getValue()))
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // Integer - total num of events in group
        // Set<String> - contextIds
        List<Pair<Integer, Set<String>>> groups = new ArrayList<>();

        int totalNumOfItems = 0;
        int start = 0;
        int end = contextIdToNumOfItemsList.size() - 1;

        while (((contextIdToNumOfItemsList.size() % 2 == 0 && (end > start)) || (contextIdToNumOfItemsList.size() % 2 != 0 && (end >= start)))) {
            Set<String> contextIds = new HashSet<>();
            Pair<String, Integer> first = contextIdToNumOfItemsList.get(start);
            Pair<String, Integer> last = contextIdToNumOfItemsList.get(end);
            contextIds.add(last.getKey());
            totalNumOfItems = last.getValue();

            while (totalNumOfItems + first.getValue() <= pageSize && contextIds.size() + 1 <= maxGroupSize &&
                    ((contextIdToNumOfItemsList.size() % 2 == 0 && (end > start)) || (contextIdToNumOfItemsList.size() % 2 != 0 && (end >= start)))) {
                totalNumOfItems += first.getValue();
                contextIds.add(first.getKey());
                start++;
                first = contextIdToNumOfItemsList.get(start);
            }

            Pair<Integer, Set<String>> totalNumOfItemsToContextIdsPair = new Pair<>(totalNumOfItems, contextIds);
            groups.add(totalNumOfItemsToContextIdsPair);
            end--;
        }
        return groups;
    }
}


