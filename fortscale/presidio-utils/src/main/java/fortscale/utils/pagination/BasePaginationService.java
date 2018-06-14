package fortscale.utils.pagination;

import fortscale.utils.data.Pair;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.*;

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
public abstract class BasePaginationService<T> {
    // num of events in page
    private int pageSize;
    // num of context ids in group
    private int maxGroupSize;

    public BasePaginationService(int pageSize, int maxGroupSize) {
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
    }

    /**
     * Creates groups, which contain pair of total num of events and set of contextIds.
     * <p>
     * In order to minimize the number of context ids in each group and to minimize the number of groups:
     * pop out context id with the largest amount of events and pop out context ids with smallest amount of events.
     *
     * @param contextIdToNumOfItemsList list of ContextIdToNumOfItems objects,each object contains context id and num of events
     * @return list num of events in group and set of contextId of pairs
     * <p>
     * Examples:
     * pageSize=3, maxGroupSize=2
     * case 1:
     * contextIdToNumOfItemsList: a->5 events, b-> 2 events, c-> 2 events
     * the groups should be {a},{b},{c}
     * case 2:
     * contextIdToNumOfItemsList: a->5 events, b-> 2 events, c-> 1 events
     * the groups should be {a},{b,c}
     */
    protected List<Pair<Integer, Set<String>>> getGroups(List<ContextIdToNumOfItems> contextIdToNumOfItemsList) {

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
        while (end > start || (end == start && numOfHandledContextIds == sortedList.size() - 1)) {
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
                first = sortedList.get(start);
                numOfHandledContextIds++;
            }

            Pair<Integer, Set<String>> totalNumOfItemsToContextIdsPair = new Pair<>(totalNumOfItems, contextIds);
            groups.add(totalNumOfItemsToContextIdsPair);
            end--;
        }
        return groups;
    }

    protected int getPageSize() {
        return pageSize;
    }

    protected void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected int getMaxGroupSize() {
        return maxGroupSize;
    }

    protected void setMaxGroupSize(int maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }
}
