package presidio.ade.domain.pagination.impl;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreImplMongo;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * Implements PageIterator.
 *
 * @param <T> (e.g: EnrichedDlpFileRecord)
 */
public class EnrichedRecordPageIterator<T extends EnrichedRecord> implements PageIterator<T> {

    private TimeRange timeRange;
    private String dataSource;
    private String contextType;
    private int currentPage;
    private int totalAmountOfPages;
    private EnrichedDataStore store;
    private int pageSize;
    private int totalNumOfItems;


    private Set<String> contextIdsList;

    /**
     * @param timeRange
     * @param contextType context type (e.g:NORMALIZED_USERNAME_FIELD, NORMALIZED_SRC_MACHINE_FIELD)
     * @param dataSource
     * @param contextIdsList  list of context ids
     * @param store
     * @param pageSize
     * @param totalNumOfItems
     */
    public EnrichedRecordPageIterator(TimeRange timeRange, String contextType, String dataSource, Set<String> contextIdsList, EnrichedDataStore store, int pageSize, int totalNumOfItems) {
        this.currentPage = 0;
        this.timeRange = timeRange;
        this.contextType = contextType;
        this.dataSource = dataSource;
        this.contextIdsList = contextIdsList;
        this.store = store;
        this.pageSize = pageSize;
        this.totalNumOfItems = totalNumOfItems;
        this.totalAmountOfPages = (int) Math.ceil((double) totalNumOfItems / pageSize);
    }

    @Override
    public boolean hasNext() {
        return this.currentPage < this.totalAmountOfPages;
    }

    /**
     * Call to the store with meta date(data source etc...), list of context ids, num of items to skip and num of items to read.
     *
     * @return list of <T>
     */
    @Override
    public List<T> next() {
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(this.dataSource, this.timeRange.getStart(), this.timeRange.getEnd());
        int numOfItemsToRead = getNumOfItemsToRead();
        int numOfItemsToSkip = this.currentPage * this.pageSize;
        this.currentPage++;
        return this.store.readRecords(enrichedRecordsMetadata, this.contextIdsList, contextType, numOfItemsToSkip, numOfItemsToRead);

    }

    /**
     * Calculate num of items to read
     * @return num of items to read
     */
    private int getNumOfItemsToRead() {
        int numOfItemsToRead = this.pageSize;
        if (this.currentPage == this.totalAmountOfPages - 1) {
            numOfItemsToRead = this.totalNumOfItems - this.pageSize * (this.totalAmountOfPages - 1);
        }
        return numOfItemsToRead;
    }


}
