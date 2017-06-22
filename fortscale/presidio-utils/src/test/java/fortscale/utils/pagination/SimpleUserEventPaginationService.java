package fortscale.utils.pagination;

import fortscale.utils.pagination.events.SimpleUserEvent;
import fortscale.utils.pagination.impl.SimpleUserEventPageIterator;
import fortscale.utils.pagination.store.SimpleUserEventStore;
import fortscale.utils.time.TimeRange;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleUserEventPaginationService extends PaginationService<SimpleUserEvent> {

    private SimpleUserEventStore store;

    public SimpleUserEventPaginationService(SimpleUserEventStore store, int pageSize, int maxGroupSize) {
        super(pageSize, maxGroupSize);
        this.store = store;
    }

    @Override
    protected List<ContextIdToNumOfEvents> getContextIdToNumOfItemsList(String dataSource, TimeRange timeRange) {

        Map<String, List<SimpleUserEvent>> map = this.store.getSimpleUserEventsMap();

        Map<String, Integer> contextIdToNumOfItemsMap = map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().size()));

        List<ContextIdToNumOfEvents> contextIdToNumOfItemsList = contextIdToNumOfItemsMap.entrySet().stream().map(x-> new ContextIdToNumOfEvents(x.getKey(),x.getValue())).collect(Collectors.toList());

        return contextIdToNumOfItemsList;
    }

    /**
     * The getPageIterator method use the SimpleUserEventStore in order to simplify the Test.
     *
     * @param dataSource
     * @param timeRange
     * @param contextIds
     * @param totalNumOfItems
     * @return
     */
    @Override
    protected <U extends SimpleUserEvent> PageIterator<U> createPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        //  List<U> list of events
        List<List<U>> pageToSimpleUserEventsList = new ArrayList<>();

        List<U> simpleUserEventsList = this.store.getListOfSimpleUserEvents(contextIds);

        for (int i = 0; i < totalNumOfItems; i += getPageSize()) {
            int fromIndex = i;
            int toIndex = (i + getPageSize() < totalNumOfItems) ? i + getPageSize() : totalNumOfItems;
            pageToSimpleUserEventsList.add(simpleUserEventsList.subList(fromIndex, toIndex));
        }

        PageIterator<U> pageIterator = new SimpleUserEventPageIterator<>(pageToSimpleUserEventsList);
        return pageIterator;
    }


    @Override
    protected void ensureContextAndDateTimeIndex(String dataSource) {
    }

}
