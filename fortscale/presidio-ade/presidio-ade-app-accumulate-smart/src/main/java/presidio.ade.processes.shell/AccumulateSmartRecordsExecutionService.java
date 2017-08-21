package presidio.ade.processes.shell;

import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.aggr.smart.SmartRecordDataReader;
import presidio.ade.processes.shell.accumulate.AccumulateSmartRecordsService;

import java.time.Instant;

public class AccumulateSmartRecordsExecutionService {
    private static Logger logger = LoggerFactory.getLogger(AccumulateSmartRecordsExecutionService.class);

    private final SmartRecordDataReader smartRecordDataReader;
    private final SmartAccumulationsCache smartAccumulationsCache;
    private final SmartAccumulationDataStore smartAccumulationDataStore;
    private int pageSize;
    private int maxGroupSize;

    public AccumulateSmartRecordsExecutionService(int pageSize, int maxGroupSize, SmartRecordDataReader smartRecordDataReader, SmartAccumulationDataStore smartAccumulationDataStore,
                                                  SmartAccumulationsCache smartAccumulationsCache) {

        this.smartAccumulationsCache = smartAccumulationsCache;
        this.smartRecordDataReader = smartRecordDataReader;
        this.smartAccumulationDataStore = smartAccumulationDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
    }

    public void run(String configurationName, Instant startDate, Instant endDate, Double accumulationStrategy) throws Exception {
        //strategy for accumulations
        FixedDurationStrategy accumulationDuration = FixedDurationStrategy.fromSeconds(accumulationStrategy.longValue());
        AccumulateSmartRecordsService accumulateSmartRecordsService = new AccumulateSmartRecordsService(accumulationDuration, smartRecordDataReader, pageSize, maxGroupSize, smartAccumulationsCache, smartAccumulationDataStore);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        accumulateSmartRecordsService.execute(timeRange, configurationName);
    }

    public void clean(String configurationName, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }
}

