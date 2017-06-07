package fortscale.utils.pagination.impl;

import fortscale.utils.pagination.PageIterator;
import java.util.List;

/**
 * simpleUserEventList is list of SimpleUserEvent lists, where each list represent the events that exist in page.
 * The next method does not use the SimpleUserStore in order to simplify the Test.
 * However the next method in PageIteratorImpl should use store.
 *
 * @param <SimpleUserEvent>
 */
public class SimpleUserPageIterator<SimpleUserEvent> implements PageIterator<SimpleUserEvent> {

    private int currentPage;
    private int totalAmountOfPages;
    private List<List<SimpleUserEvent>> simpleUserEventList; //change to arraylist and comment

    public SimpleUserPageIterator(List<List<SimpleUserEvent>> list){
        this.currentPage = 0;
        this.simpleUserEventList = list;
        this.totalAmountOfPages = list.size();
    }

    @Override
    public boolean hasNext() {
        return this.currentPage <  this.totalAmountOfPages;
    }

    @Override
    public List<SimpleUserEvent> next() {
        List<SimpleUserEvent> simpleUserEventsList = this.simpleUserEventList.get(currentPage);
        currentPage++;
        return simpleUserEventsList;
    }
}
