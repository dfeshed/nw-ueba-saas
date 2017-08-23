package presidio.ade.domain.pagination.smart;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataReader;

import java.util.*;

public class ScoreThresholdSmartPaginationService extends SmartPaginationService {


    private static final Logger logger = Logger.getLogger(ScoreThresholdSmartPaginationService.class);

    public ScoreThresholdSmartPaginationService(SmartDataReader reader, int pageSize) {
        super(reader, pageSize, Integer.MAX_VALUE);
    }

    /**
     * @param timeRange           the time range
     * @param smartScoreThreshold score threshold
     * @return list of PageIterator<SmartRecord>
     */
    public List<PageIterator<SmartRecord>> getPageIterators(String configurationName, TimeRange timeRange, int smartScoreThreshold) {
        //Validate if indexes exist, otherwise add them.
        ensureContextIdIndex(configurationName);
        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = getContextIdToNumOfItemsList(configurationName, timeRange);
        setMaxGroupSize(contextIdToNumOfItemsList.size());
        return getScoreThresholdPageIterators(configurationName, timeRange, contextIdToNumOfItemsList, smartScoreThreshold);
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
        List<List<PageIterator<SmartRecord>>> pageIteratorsPerCollection = new ArrayList<>();

        for (String configurationName : configurationNames) {
            List<PageIterator<SmartRecord>> pageIterators = getPageIterators(configurationName, timeRange, smartScoreThreshold);
            pageIteratorsPerCollection.add(pageIterators);
        }
        return new AllSmartCollectionsPageIterators(pageIteratorsPerCollection);
    }

    /**
     * @return list of contextId to num of items
     */
    private Set<String> getSmartConfigurationNames() {
        return reader.getAllSmartConfigurationNames();
    }

}
