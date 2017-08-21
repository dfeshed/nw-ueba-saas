package presidio.ade.domain.pagination.smart;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.aggr.smart.SmartRecordDataReader;
import presidio.ade.domain.store.aggr.smart.SmartRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * Implements PageIterator.
 * SmartRecordPageIterator use smartReader to get list of smart records.
 * By using num of items to skip and num of items to read, reader get the records for current iteration.
 */
public class SmartRecordPageIterator implements PageIterator<SmartRecord> {

    private TimeRange timeRange;
    private String configurationName;
    private int currentPage;
    private int totalAmountOfPages;
    private SmartRecordDataReader reader;
    private int pageSize;
    private Set<String> contextIds;

    /**
     * @param timeRange         time range
     * @param configurationName smart configuration name
     * @param contextIds        set of context ids
     * @param reader            smart reader
     * @param pageSize          num of events in each page
     */
    public SmartRecordPageIterator(TimeRange timeRange, String configurationName, Set<String> contextIds, SmartRecordDataReader reader, int pageSize, int totalAmountOfPages) {
        this.currentPage = 0;
        this.timeRange = timeRange;
        this.configurationName = configurationName;
        this.contextIds = contextIds;
        this.reader = reader;
        this.pageSize = pageSize;
        this.totalAmountOfPages = totalAmountOfPages;
    }

    @Override
    public boolean hasNext() {
        return this.currentPage < this.totalAmountOfPages;
    }

    /**
     * Call to the reader with smart meta date(configurationName etc...), list of context ids, num of items to skip and num of items to read.
     *
     * @return list of <U>
     */
    @Override
    public List<SmartRecord> next() {
        SmartRecordsMetadata smartRecordsMetadata = new SmartRecordsMetadata(configurationName, timeRange.getStart(), timeRange.getEnd());
        int numOfItemsToSkip = this.currentPage * this.pageSize;
        this.currentPage++;

        return this.reader.readRecords(smartRecordsMetadata, this.contextIds, numOfItemsToSkip, this.pageSize);
    }

}
