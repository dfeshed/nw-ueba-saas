package presidio.ade.domain.pagination.smart;

import fortscale.utils.pagination.PageIterator;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements PageIterator.
 * Go over all lists of PageIterators and get the next page.
 *
 * See reference for test: ScoreThresholdSmartPaginationServiceTest
 */
public class AllSmartCollectionsPageIterators implements PageIterator<SmartRecord> {


    //List of (lists of pageIterators).
    //Each internal List of pageIterators related to collection.
    private List<List<PageIterator<SmartRecord>>> pageIteratorsPerCollection;

    private List<PageIterator<SmartRecord>> pageIteratorList;
    private PageIterator<SmartRecord> pageIterator;
    private int currentPageIterator;
    private int currentCollection;


    /**
     * @param collectionToPageIterators smart pageIterator
     */
    public AllSmartCollectionsPageIterators(List<List<PageIterator<SmartRecord>>>  collectionToPageIterators) {
        this.pageIteratorsPerCollection = collectionToPageIterators;

        this.currentCollection = 1;
        this.pageIteratorList = collectionToPageIterators.get(currentCollection - 1);

        this.currentPageIterator = 1;
        this.pageIterator = pageIteratorList.get(currentPageIterator - 1);
    }

    @Override
    public boolean hasNext() {
        return this.pageIterator.hasNext() || currentPageIterator < pageIteratorList.size() || currentCollection < pageIteratorsPerCollection.size();
    }

    /**
     * @return list of SmartRecord
     */
    @Override
    public List<SmartRecord> next() {
        //If page iterator has not more pages and there no more pageIterators => get next list of page iterators.
        if(!pageIterator.hasNext() && currentPageIterator >= pageIteratorList.size() && currentCollection < pageIteratorsPerCollection.size()){
            pageIteratorList = pageIteratorsPerCollection.get(currentCollection);
            currentCollection++;
            currentPageIterator=1;
            pageIterator = pageIteratorList.get(currentPageIterator - 1);
        }
        //If page iterator has not pages => get next  page iterator.
        else if (!pageIterator.hasNext() && currentPageIterator < pageIteratorList.size()) {
            pageIterator = pageIteratorList.get(currentPageIterator);
            currentPageIterator++;
        }

        return pageIterator.next();
    }





}
