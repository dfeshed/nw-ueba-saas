package presidio.ade.processes.shell.accumulate;

import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.accumulator.smart.SmartAccumulatorService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.smart.SmartPaginationService;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.smart.SmartDataReader;

import java.util.*;

/**
 * Accumulate smart records between timeRange for each contextId.
 */
public class AccumulateSmartRecordsService extends AccumulationStrategyExecutor {


    private SmartDataReader reader;
    private int pageSize;
    private int maxGroupSize;
    private SmartAccumulationsCache smartAccumulationsCache;
    private SmartAccumulationDataStore smartAccumulationDataStore;

    public AccumulateSmartRecordsService(FixedDurationStrategy accumulationStrategy, SmartDataReader reader,
                                         int pageSize, int maxGroupSize,
                                         SmartAccumulationsCache smartAccumulationsCache,
                                         SmartAccumulationDataStore smartAccumulationDataStore) {
        super(accumulationStrategy);
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.smartAccumulationsCache = smartAccumulationsCache;
        this.reader = reader;
        this.smartAccumulationDataStore = smartAccumulationDataStore;
    }


    protected void executeSingleTimeRange(TimeRange timeRange, String configurationName) {

        SmartPaginationService smartPaginationService = new SmartPaginationService(reader, pageSize, maxGroupSize);
        List<PageIterator<SmartRecord>> pageIterators = smartPaginationService.getPageIterators(configurationName, timeRange);

        for (PageIterator<SmartRecord> pageIterator : pageIterators) {
            SmartAccumulatorService smartAccumulatorService = new SmartAccumulatorService(smartAccumulationsCache, timeRange);

            while (pageIterator.hasNext()) {
                List<SmartRecord> smartRecords = pageIterator.next();
                smartAccumulatorService.accumulate(smartRecords);
            }

            List<AccumulatedSmartRecord> accumulationsRecords = getAccumulatedSmartRecords();

            smartAccumulationDataStore.store(accumulationsRecords, configurationName);
        }
    }

    /**
     * get and clean accumulated records
     *
     * @return accumulated records
     */
    private List<AccumulatedSmartRecord> getAccumulatedSmartRecords() {
        List<AccumulatedSmartRecord> accumulationsCache = smartAccumulationsCache.getAllAccumulatedRecords();
        smartAccumulationsCache.clean();
        return accumulationsCache;
    }


}
