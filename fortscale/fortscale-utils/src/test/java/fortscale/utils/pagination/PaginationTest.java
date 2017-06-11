package fortscale.utils.pagination;

import fortscale.utils.pagination.events.SimpleUserEvent;
import fortscale.utils.pagination.store.SimpleUserStore;
import fortscale.utils.time.TimeRange;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PaginationTest {

    /**
     * The test creates store and paginationService instances.
     * The store contains SimpleUserEvent map, which has default values of contextId and list of SimpleUserEvent.
     * The paginationService return pageIterators.
     * The test assert contextId list, amount of events and amount of pages in each iterator.
     */
    @Test
    public void test_getListOfLeaf() {
        int pageSize = 30;
        int maxGroupSize = 4;

        //String - contextId
        // List<SimpleUserEvent> - list of SimpleUserEvent
        Map<String, List<SimpleUserEvent>> simpleUserEventsMap = new HashMap<>();
        SimpleUserStore.addDefaultValues(simpleUserEventsMap);
        SimpleUserStore store = new SimpleUserStore(simpleUserEventsMap);

        PaginationService<SimpleUserEvent> paginationService = new SimpleUserPaginationService(store, pageSize, maxGroupSize);

        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now());

        List<PageIterator<SimpleUserEvent>> pageIterators = paginationService.getPageIterators("", timeRange);

        assertTrue(pageIterators.size() == 3);

        Iterator<PageIterator<SimpleUserEvent>> simpleUserEventPageIterator = pageIterators.iterator();

        //foreach pageIterator get pages.
        // get amount of pages in iterator.
        //foreach page get contextId list and simpleUserEvents list.
        while (simpleUserEventPageIterator.hasNext()) {
            PageIterator<SimpleUserEvent> pageIterator = simpleUserEventPageIterator.next();
            List<SimpleUserEvent> simpleUserEventsList = new ArrayList<>();
            List<String> contextIdList = new ArrayList<>();
            int amountOfPages = 0;

            while (pageIterator.hasNext()) {
                List<SimpleUserEvent> list = pageIterator.next();
                amountOfPages++;
                for (SimpleUserEvent SimpleUserEvent : list) {
                    simpleUserEventsList.add(SimpleUserEvent);
                    String name = SimpleUserEvent.getName();
                    if (!contextIdList.contains(name))
                        contextIdList.add(name);
                }
            }
            assertExpectedResult(contextIdList, simpleUserEventsList, amountOfPages);
        }
    }

    /**
     * Assert amount of pages in group, amount of events in group and context ids.
     * The result according to store default values should be:
     * group_1: [h][h][h][h] = 40 events, 2 pages
     * group_2: [g,b,c,a] = 24 events, 1 page
     * group_3: [d,e,f] = 17 events, 1 page
     *
     * @param contextIdList
     * @param simpleUserEventsList
     * @param amountOfPages
     */
    public void assertExpectedResult(List<String> contextIdList, List<SimpleUserEvent> simpleUserEventsList, int amountOfPages) {

        List<String> group1 = new ArrayList<>();
        group1.add("h");
        int amountOfPagesINGroup1 = 2;
        int amountOfEventsINGroup1 = 39;

        List<String> group2 = new ArrayList<>();
        group2.add("g");
        group2.add("b");
        group2.add("c");
        group2.add("a");
        int amountOfPagesINGroup2 = 1;
        int amountOfEventsINGroup2 = 24;

        List<String> group3 = new ArrayList<>();
        group3.add("d");
        group3.add("e");
        group3.add("f");
        int amountOfPagesINGroup3 = 1;
        int amountOfEventsINGroup3 = 17;

        int testEventsNum = simpleUserEventsList.size();

        if (group1.containsAll(contextIdList)) {
            assertTrue(amountOfPagesINGroup1 == amountOfPages);
            assertTrue(amountOfEventsINGroup1 == testEventsNum);
        } else if (group2.containsAll(contextIdList)) {
            assertTrue(amountOfPagesINGroup2 == amountOfPages);
            assertTrue(amountOfEventsINGroup2 == testEventsNum);
        } else if (group3.containsAll(contextIdList)) {
            assertTrue(amountOfPagesINGroup3 == amountOfPages);
            assertTrue(amountOfEventsINGroup3 == testEventsNum);
        } else {
            assertFalse(true);
        }
    }


}
