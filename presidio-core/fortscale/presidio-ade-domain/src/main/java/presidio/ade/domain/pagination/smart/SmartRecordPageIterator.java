package presidio.ade.domain.pagination.smart;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartRecordsMetadata;

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
    private SmartDataReader reader;
    private int pageSize;
    private int smartScoreThreshold;
    private Set<String> contextIds;

    /**
     * @param timeRange         time range
     * @param configurationName smart configuration name | null
     * @param contextIds        set of context ids
     * @param reader            smart reader
     * @param pageSize          num of events in each page
     */
    public SmartRecordPageIterator(TimeRange timeRange, String configurationName, Set<String> contextIds, SmartDataReader reader, int pageSize, int totalAmountOfPages, int smartScoreThreshold) {
        this.currentPage = 0;
        this.timeRange = timeRange;
        this.configurationName = configurationName;
        this.contextIds = contextIds;
        this.reader = reader;
        this.pageSize = pageSize;
        this.totalAmountOfPages = totalAmountOfPages;
        this.smartScoreThreshold = smartScoreThreshold;
    }

    @Override
    public boolean hasNext() {
        return this.currentPage < this.totalAmountOfPages;
    }

    /**
     * Call to the reader with smart meta date(configurationName etc...), list of context ids, num of items to skip and num of items to read.
     * If configurationName undefined read records over all smart collections
     *
     * @return list of <U>
     */
    @Override
    public List<SmartRecord> next() {
        SmartRecordsMetadata smartRecordsMetadata = new SmartRecordsMetadata(configurationName, timeRange.getStart(), timeRange.getEnd());
        int numOfItemsToSkip = this.currentPage * this.pageSize;
        this.currentPage++;

        return this.reader.readRecords(smartRecordsMetadata, this.contextIds, numOfItemsToSkip, this.pageSize, this.smartScoreThreshold);
    }

}
