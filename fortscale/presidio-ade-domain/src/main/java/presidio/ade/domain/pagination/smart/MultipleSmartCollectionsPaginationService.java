package presidio.ade.domain.pagination.smart;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultipleSmartCollectionsPaginationService {
    private Collection<String> configurationNames;
    private SmartPaginationService smartPaginationService;

    public MultipleSmartCollectionsPaginationService(
            Collection<String> configurationNames, SmartDataReader smartDataReader, int pageSize, int maxGroupSize) {

        this.configurationNames = configurationNames;
        this.smartPaginationService = new SmartPaginationService(smartDataReader, pageSize, maxGroupSize);
    }

    /**
     * Get one page iterator that iterates all the smart records of the given configurations:
     * 1. Get a list of page iterators per each configuration.
     * 2. Merge all the page iterator lists into one list.
     * 3. Return a new page iterator that wraps this list.
     *
     * @param timeRange           time range
     * @param smartScoreThreshold smart score threshold
     * @return {@link MultipleSmartCollectionsPageIterator}
     */
    public PageIterator<SmartRecord> getPageIterator(TimeRange timeRange, int smartScoreThreshold) {
        List<PageIterator<SmartRecord>> allPageIterators = new ArrayList<>();

        for (String configurationName : configurationNames) {
            List<PageIterator<SmartRecord>> pageIterators = smartPaginationService
                    .getPageIterators(configurationName, timeRange, smartScoreThreshold);
            allPageIterators.addAll(pageIterators);
        }

        return new MultipleSmartCollectionsPageIterator(allPageIterators);
    }
}
