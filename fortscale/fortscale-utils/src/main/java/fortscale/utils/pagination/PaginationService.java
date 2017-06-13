package fortscale.utils.pagination;

import fortscale.utils.time.TimeRange;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class PaginationService<T> {

    private int pageSize;
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
    public List<PageIterator<T>> getPageIterators(String dataSource, TimeRange timeRange) {

        List<PageIterator<T>> pageIteratorList = new ArrayList<>();
        Map<String, Integer> contextIdToNumOfItemsMap = getContextIdToNumOfItemsMap(dataSource, timeRange);
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsMap);

        Iterator<Pair<Integer, Set<String>>> it = groups.iterator();
        while (it.hasNext()) {
            Pair<Integer, Set<String>> pair = it.next();
            Set<String> contextIds = pair.getValue();
            int totalNumOfItems = pair.getKey();
            PageIterator<T> pageIterator = getPageIterator(dataSource, timeRange, contextIds, totalNumOfItems);
            pageIteratorList.add(pageIterator);
        }

        return pageIteratorList;
    }

    /**
     * Create map of context ids and num of events based on timeRange and dataSource.
     *
     * @param dataSource
     * @param timeRange
     * @return
     */
    protected abstract Map<String, Integer> getContextIdToNumOfItemsMap(String dataSource, TimeRange timeRange);

    /**
     * Create pageIterator
     *
     * @param dataSource
     * @param timeRange
     * @param contextIds
     * @param totalNumOfItems num of events in PageIterator
     * @return
     */
    protected abstract PageIterator<T> getPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems);

    /**
     * Validate the store indexes
     */
    protected abstract void validateIndexes();


    /**
     * Creates groups, which contain pair of total num of events in group and set of contextId:
     * Sort the contextIdToNumOfItemsMap.
     * Add the last contextId to set.
     * while num of events less than pageSize and contextIds set amount less than maxGroupSize => Add first contextIds to set.
     *
     * @param contextIdToNumOfItemsMap map of context id to num of events
     * @return list num of events in group and set of contextId of pairs
     */
    private List<Pair<Integer, Set<String>>> getGroups(Map<String, Integer> contextIdToNumOfItemsMap) {
        contextIdToNumOfItemsMap = contextIdToNumOfItemsMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));

        // Integer - total num of events in group
        // Set<String> - contextIds
        List<Pair<Integer, Set<String>>> groups = new ArrayList<>();

        int totalNumOfItems = 0;
        int start = 0;
        int end = contextIdToNumOfItemsMap.size() - 1;

        while (end > start) {
            Set<String> contextIds = new HashSet<>();
            Map.Entry<String, Integer> first = (Map.Entry) contextIdToNumOfItemsMap.entrySet().toArray()[start];
            Map.Entry<String, Integer> last = (Map.Entry) contextIdToNumOfItemsMap.entrySet().toArray()[end];
            contextIds.add(last.getKey());
            totalNumOfItems = last.getValue();

            while (totalNumOfItems + first.getValue() <= pageSize && contextIds.size() + 1 <= maxGroupSize && end > start) {
                totalNumOfItems += first.getValue();
                contextIds.add(first.getKey());
                start++;
                first = (Map.Entry) contextIdToNumOfItemsMap.entrySet().toArray()[start];
            }

            Pair<Integer, Set<String>> totalNumOfItemsToContextIdsPair = new Pair<>(totalNumOfItems, contextIds);
            groups.add(totalNumOfItemsToContextIdsPair);
            end--;
        }

        return groups;
    }
}


