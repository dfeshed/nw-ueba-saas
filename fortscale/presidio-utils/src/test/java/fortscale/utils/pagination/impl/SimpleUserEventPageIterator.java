package fortscale.utils.pagination.impl;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.pagination.events.SimpleUserEvent;

import java.util.List;

/**
 * simpleUserEventList is list of pages.
 * The next method does not use the SimpleUserEventStore in order to simplify the Test.
 * However the next method of PageIterator implementation should use store.
 */
public class SimpleUserEventPageIterator<U extends SimpleUserEvent> implements PageIterator<U> {

    private int currentPage;
    private int totalAmountOfPages;
    private List<List<U>> simpleUserEventList;

    public SimpleUserEventPageIterator(List<List<U>> list) {
        this.currentPage = 0;
        this.simpleUserEventList = list;
        this.totalAmountOfPages = list.size();
    }

    @Override
    public boolean hasNext() {
        return this.currentPage < this.totalAmountOfPages;
    }

    @Override
    public List<U> next() {
        List<U> simpleUserEventsList = this.simpleUserEventList.get(currentPage);
        currentPage++;
        return simpleUserEventsList;
    }
}
