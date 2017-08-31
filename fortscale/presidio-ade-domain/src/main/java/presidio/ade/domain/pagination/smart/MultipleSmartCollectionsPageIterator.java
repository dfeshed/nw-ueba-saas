package presidio.ade.domain.pagination.smart;

import fortscale.utils.pagination.PageIterator;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.List;

/**
 * Implements PageIterator.
 * Go over all lists of PageIterators and get the next page.
 * <p>
 * See test for reference: MultipleSmartCollectionsPaginationServiceTest
 */
public class MultipleSmartCollectionsPageIterator implements PageIterator<SmartRecord> {
    //list of page iterators.
    private List<PageIterator<SmartRecord>> pageIteratorList;
    private PageIterator<SmartRecord> pageIterator;
    private int currentPageIteratorIndex;

    /**
     * @param pageIteratorList smart pageIterator
     */
    public MultipleSmartCollectionsPageIterator(List<PageIterator<SmartRecord>> pageIteratorList) {
        this.pageIteratorList = pageIteratorList;

        //initialize pageIterator, if pageIteratorList is not empty
        if (pageIteratorList.size() > 0) {
            this.currentPageIteratorIndex = 1;
            this.pageIterator = pageIteratorList.get(currentPageIteratorIndex - 1);
        }
    }

    @Override
    public boolean hasNext() {
        return pageIteratorList.size() > 0 &&
                (this.pageIterator.hasNext() || currentPageIteratorIndex < pageIteratorList.size());
    }

    /**
     * @return list of SmartRecord
     */
    @Override
    public List<SmartRecord> next() {
        //If page iterator has not pages => get next  page iterator.
        if (!pageIterator.hasNext() && currentPageIteratorIndex < pageIteratorList.size()) {
            pageIterator = pageIteratorList.get(currentPageIteratorIndex);
            currentPageIteratorIndex++;
        }

        return pageIterator.next();
    }
}
