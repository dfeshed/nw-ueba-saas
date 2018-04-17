package fortscale.utils.pagination;

import fortscale.utils.pagination.events.SimpleUserEvent;
import fortscale.utils.pagination.store.SimpleUserEventStore;
import fortscale.utils.time.TimeRange;
import fortscale.utils.data.Pair;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PaginationServiceTest {

    private List<Pair<Set<String>, Pair<Integer, Integer>>> list;
    private static final int PAGE_SIZE = 30;
    private static final int MAX_GROUP_SIZE = 4;

    @Before
    public void initialize() {
        list = new ArrayList<>();

        Pair<Integer, Integer> numOfPagesToNumOfEvents;
        Pair<Set<String>, Pair<Integer, Integer>> contextIdsPair1, contextIdsPair2, contextIdsPair3;

        Set<String> group1 = new HashSet<>();
        group1.add("h");
        int amountOfPagesINGroup1 = 2;
        int amountOfEventsINGroup1 = 40;
        numOfPagesToNumOfEvents = new Pair<>(amountOfPagesINGroup1, amountOfEventsINGroup1);
        contextIdsPair1 = new Pair<>(group1, numOfPagesToNumOfEvents);

        Set<String> group2 = new HashSet<>();
        group2.add("g");
        group2.add("b");
        group2.add("c");
        group2.add("a");
        int amountOfPagesINGroup2 = 1;
        int amountOfEventsINGroup2 = 24;
        numOfPagesToNumOfEvents = new Pair<>(amountOfPagesINGroup2, amountOfEventsINGroup2);
        contextIdsPair2 = new Pair<>(group2, numOfPagesToNumOfEvents);

        Set<String> group3 = new HashSet<>();
        group3.add("d");
        group3.add("e");
        group3.add("f");
        int amountOfPagesINGroup3 = 1;
        int amountOfEventsINGroup3 = 17;
        numOfPagesToNumOfEvents = new Pair<>(amountOfPagesINGroup3, amountOfEventsINGroup3);
        contextIdsPair3 = new Pair<>(group3, numOfPagesToNumOfEvents);

        list.add(contextIdsPair1);
        list.add(contextIdsPair2);
        list.add(contextIdsPair3);

    }

    /**
     * The test creates store and paginationService instances.
     * The store contains SimpleUserEvent map, which has default values of contextId and list of SimpleUserEvent.
     * The paginationService return pageIterators.
     * The test assert contextId list, amount of events and amount of pages in each iterator.
     */
    @Test
    public void test_pagination_service() {

        //String - contextId
        // List<SimpleUserEvent> - list of SimpleUserEvent
        Map<String, List<SimpleUserEvent>> simpleUserEventsMap = new HashMap<>();
        SimpleUserEventStore.addDefaultValues(simpleUserEventsMap);
        SimpleUserEventStore<SimpleUserEvent> store = new SimpleUserEventStore<>(simpleUserEventsMap);

        PaginationService<SimpleUserEvent> paginationService = new SimpleUserEventPaginationService(store, PAGE_SIZE, MAX_GROUP_SIZE);

        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now());

        List<PageIterator<SimpleUserEvent>> pageIterators = paginationService.getPageIterators("", timeRange);

        assertTrue(pageIterators.size() == 3);

        //foreach pageIterator get pages.
        // get amount of pages in iterator.
        //foreach page get contextId list and simpleUserEvents list.
        for (PageIterator<SimpleUserEvent> pageIterator : pageIterators) {
            List<SimpleUserEvent> simpleUserEventsList = new ArrayList<>();
            Set<String> contextIdSet = new HashSet<>();
            int amountOfPages = 0;

            while (pageIterator.hasNext()) {
                List<SimpleUserEvent> list = pageIterator.next();
                amountOfPages++;
                for (SimpleUserEvent SimpleUserEvent : list) {
                    simpleUserEventsList.add(SimpleUserEvent);
                    String name = SimpleUserEvent.getName();
                    contextIdSet.add(name);
                }
            }
            assertExpectedResult(contextIdSet, simpleUserEventsList, amountOfPages);
        }
        assertTrue(list.size() == 0);
    }

    /**
     * Assert amount of pages in group, amount of events in group and context ids.
     * The result according to store default values should be:
     * group_1: [h][h][h][h] = 40 events, 2 pages
     * group_2: [g,b,c,a] = 24 events, 1 page
     * group_3: [d,e,f] = 17 events, 1 page
     *
     * @param contextIdSet
     * @param simpleUserEventsList
     * @param amountOfPages
     */
    public void assertExpectedResult(Set<String> contextIdSet, List<SimpleUserEvent> simpleUserEventsList, int amountOfPages) {

        Pair<Set<String>, Pair<Integer, Integer>> itemToRemove = null;
        for (Pair<Set<String>, Pair<Integer, Integer>> pair : list) {
            Set<String> group = pair.getKey();
            if (group.containsAll(contextIdSet)) {
                Pair<Integer, Integer> numOfPagesToNumOfEvents = pair.getValue();
                int amountOfPagesInGroup = numOfPagesToNumOfEvents.getKey();
                int amountOfEventsInGroup = numOfPagesToNumOfEvents.getValue();
                int testEventsNum = simpleUserEventsList.size();
                assertTrue(amountOfPagesInGroup == amountOfPages);
                assertTrue(amountOfEventsInGroup == testEventsNum);
                itemToRemove = pair;
                break;
            }
        }

        if (itemToRemove != null) {
            list.remove(itemToRemove);
        } else {
            assertFalse(true);
        }
    }
}
