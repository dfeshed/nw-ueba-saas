package presidio.ade.processes.shell;

import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.processes.shell.accumulate.AccumulateSmartRecordsService;

import java.time.Instant;

public class AccumulateSmartRecordsExecutionService {
    private final SmartDataReader smartDataReader;
    private final SmartAccumulationsCache smartAccumulationsCache;
    private final SmartAccumulationDataStore smartAccumulationDataStore;
    private int pageSize;
    private int maxGroupSize;
    private TtlService ttlService;

    public AccumulateSmartRecordsExecutionService(int pageSize, int maxGroupSize, SmartDataReader smartDataReader, SmartAccumulationDataStore smartAccumulationDataStore,
                                                  SmartAccumulationsCache smartAccumulationsCache, TtlService ttlService) {

        this.smartAccumulationsCache = smartAccumulationsCache;
        this.smartDataReader = smartDataReader;
        this.smartAccumulationDataStore = smartAccumulationDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.ttlService = ttlService;
    }

    public void run(String configurationName, Instant startDate, Instant endDate, Double accumulationStrategy) throws Exception {
        //strategy for accumulations
        FixedDurationStrategy accumulationDuration = FixedDurationStrategy.fromSeconds(accumulationStrategy.longValue());
        AccumulateSmartRecordsService accumulateSmartRecordsService = new AccumulateSmartRecordsService(accumulationDuration, smartDataReader, pageSize, maxGroupSize, smartAccumulationsCache, smartAccumulationDataStore);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        accumulateSmartRecordsService.execute(timeRange, configurationName);

        ttlService.cleanupCollections(startDate);
    }

    public void clean(String configurationName, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }
}

