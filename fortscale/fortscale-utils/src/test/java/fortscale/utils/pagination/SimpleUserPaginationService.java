package fortscale.utils.pagination;

import fortscale.utils.pagination.events.SimpleUserEvent;
import fortscale.utils.pagination.impl.SimpleUserPageIterator;
import fortscale.utils.pagination.store.SimpleUserStore;
import fortscale.utils.time.TimeRange;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleUserPaginationService extends PaginationService<SimpleUserEvent>{

    private SimpleUserStore store;

    public SimpleUserPaginationService(SimpleUserStore store, int pageSize, int maxGroupSize){
        super(pageSize, maxGroupSize);
        this.store = store;
    }

    @Override
    protected Map<String, Integer> getContextIdToNumOfItemsMap(String dataSource, TimeRange timeRange) {

        Map<String, List<SimpleUserEvent>> map = this.store.getSimpleUserEventsMap();
        Map<String, Integer> contextIdToNumOfItemsMap =  map.entrySet().stream().collect(Collectors.toMap(entry  -> entry.getKey(), entry -> entry.getValue().size()));
        return contextIdToNumOfItemsMap;
    }

     /**
      *
      * The getPageIterator method use the SimpleUserStore in order to simplify the Test.
      *
     * @param dataSource
     * @param timeRange
     * @param contextIds
     * @param totalNumOfItems
     * @return
     */
    @Override
    protected PageIterator<SimpleUserEvent> getPageIterator(String dataSource, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems) {
        //Integer - page index
        //  List<SimpleUserEvent> - list of events
        List<List<SimpleUserEvent>> pageToSimpleUserEventsList = new ArrayList<>();

        List<SimpleUserEvent> simpleUserEventsList = this.store.getListOfSimpleUserEvents(contextIds);
        if(totalNumOfItems <= getPageSize()){
            pageToSimpleUserEventsList.add(simpleUserEventsList);
        }
        else{
            for(int i=0 ; i < totalNumOfItems;i+=getPageSize()){
                int fromIndex = i;
                int toIndex = (i+getPageSize() < totalNumOfItems ) ?i+getPageSize() : totalNumOfItems -1;
                pageToSimpleUserEventsList.add(simpleUserEventsList.subList(fromIndex,toIndex));
            }
        }

        PageIterator<SimpleUserEvent> pageIterator = new SimpleUserPageIterator<>(pageToSimpleUserEventsList);

        return pageIterator;
    }

    @Override
    protected void validateIndexes(){}

}
