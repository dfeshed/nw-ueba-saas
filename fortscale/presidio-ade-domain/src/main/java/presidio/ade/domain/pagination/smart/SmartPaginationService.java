package presidio.ade.domain.pagination.smart;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.BasePaginationService;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import fortscale.utils.data.Pair;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartRecordsMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SmartPaginationService extends BasePaginationService<SmartRecord> {


    private static final Logger logger = Logger.getLogger(SmartPaginationService.class);
    protected SmartDataReader reader;
    private static final int SCORE_THRESHOLD_DEFAULT = Integer.MIN_VALUE;

    public SmartPaginationService(SmartDataReader reader, int pageSize, int maxGroupSize) {
        super(pageSize, maxGroupSize);
        this.reader = reader;
    }

    /**
     * Create pageIterators:
     * Get map of contextId and number of records for each contextId.
     * Get groups by the map, the group list contains pairs of records amount and contextIds.
     * Create pageIterator for each group.
     *
     * @param configurationName configuration name
     * @param timeRange         the time range
     * @return list of PageIterators
     */
    public List<PageIterator<SmartRecord>> getPageIterators(String configurationName, TimeRange timeRange) {
        return getScoreThresholdPageIterators(configurationName, timeRange, SCORE_THRESHOLD_DEFAULT);
    }


    /**
     * Get List of pageIterators
     * @param configurationName configuration name
     * @param timeRange           the time range
     * @param smartScoreThreshold the scores should be greater than or equal to this threshold
     * @return list of PageIterator<SmartRecord>
     */
    public List<PageIterator<SmartRecord>> getPageIterators(String configurationName, TimeRange timeRange, int smartScoreThreshold) {
        return getScoreThresholdPageIterators(configurationName, timeRange, smartScoreThreshold);
    }


    /**
     * Get List of pageIterators filtered by score.
     * @param configurationName configuration name
     * @param timeRange time range
     * @param smartScoreThreshold the scores should be greater than or equal to this threshold
     * @return list of pageIterators.
     */
    protected List<PageIterator<SmartRecord>> getScoreThresholdPageIterators(String configurationName, TimeRange timeRange, int smartScoreThreshold) {
        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = getContextIdToNumOfItemsList(configurationName, timeRange, smartScoreThreshold);

        //groups is a list, where each group contains pair of total num of records and set of contextId.
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsList);
        List<PageIterator<SmartRecord>> pageIteratorList = new ArrayList<>(groups.size());

        //create pageIterator of each group
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            int totalNumOfItems = group.getKey();
            PageIterator<SmartRecord> pageIterator = createPageIterator(configurationName, timeRange, contextIds, totalNumOfItems, smartScoreThreshold);
            pageIteratorList.add(pageIterator);
        }

        logger.debug("Num of page iterators is: {}", pageIteratorList.size());
        return pageIteratorList;
    }

    /**
     * @param configurationName smart configuration record
     * @param timeRange         timeRange
     * @param smartScoreThreshold the scores should be greater than or equal to this threshold
     * @return list of contextId to num of items
     */
    protected List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String configurationName, TimeRange timeRange, int smartScoreThreshold) {
        SmartRecordsMetadata smartRecordsMetadata = new SmartRecordsMetadata(configurationName, timeRange.getStart(), timeRange.getEnd());
        return this.reader.aggregateContextIdToNumOfEvents(smartRecordsMetadata, smartScoreThreshold);
    }


    /**
     * @param configurationName smart configuration record
     * @param timeRange         timeRange
     * @param contextIds        available context ids in PageIterator
     * @param totalNumOfItems   total num of records in page
     * @param smartScoreThreshold the scores should be greater than or equal to this threshold
     * @return PageIterator
     */
    protected PageIterator<SmartRecord> createPageIterator(String configurationName, TimeRange timeRange, Set<String> contextIds, int totalNumOfItems, int smartScoreThreshold) {
        int totalAmountOfPages = (int) Math.ceil((double) totalNumOfItems / this.getPageSize());
        logger.debug("Num of pages is: {}", totalAmountOfPages);
        return new SmartRecordPageIterator(timeRange, configurationName, contextIds, this.reader, this.getPageSize(), totalAmountOfPages, smartScoreThreshold);
    }
}
