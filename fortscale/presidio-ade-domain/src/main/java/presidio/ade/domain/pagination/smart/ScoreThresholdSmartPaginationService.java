package presidio.ade.domain.pagination.smart;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataReader;

import java.util.*;

public class ScoreThresholdSmartPaginationService {


    private static final Logger logger = Logger.getLogger(ScoreThresholdSmartPaginationService.class);
    private static final int MAX_GROUP_SIZE_DEFAULT = 100;
    private SmartPaginationService smartPaginationService;
    private SmartDataReader reader;

    public ScoreThresholdSmartPaginationService(SmartDataReader reader, int pageSize) {
        smartPaginationService = new SmartPaginationService(reader, pageSize, MAX_GROUP_SIZE_DEFAULT);
        this.reader = reader;
    }

    /**
     *
     * Get PageIterator, which go over pages along all smart collections:
     *
     * Get all smarts configurationNames.
     * Get PageIterators per each configurationName.
     * Create list of pageIterators lists.
     *
     * @param timeRange           time range
     * @param smartScoreThreshold score threshold
     * @return page iterator
     */
    public PageIterator<SmartRecord> getPageIterator(TimeRange timeRange, int smartScoreThreshold) {
        Set<String> configurationNames = getSmartConfigurationNames();
        List<PageIterator<SmartRecord>> pageIteratorList = new ArrayList<>();

        for (String configurationName : configurationNames) {
            List<PageIterator<SmartRecord>> pageIterators = smartPaginationService.getPageIterators(configurationName, timeRange, smartScoreThreshold);
            pageIteratorList.addAll(pageIterators);
        }
        return new AllSmartCollectionsPageIterators(pageIteratorList);
    }

    /**
     * @return list of contextId to num of items
     */
    private Set<String> getSmartConfigurationNames() {
        return reader.getAllSmartConfigurationNames();
    }

}
