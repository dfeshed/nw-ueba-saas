package presidio.ade.processes.shell;

import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.processes.shell.accumulate.AccumulateSmartRecordsService;

import java.time.Instant;

public class AccumulateSmartRecordsExecutionService {
    private static String CONFIGURATION_NAME = "configuration_name";
    private final SmartDataReader smartDataReader;
    private final SmartAccumulationsCache smartAccumulationsCache;
    private final SmartAccumulationDataStore smartAccumulationDataStore;
    private int pageSize;
    private int maxGroupSize;
    private StoreManager storeManager;

    public AccumulateSmartRecordsExecutionService(int pageSize, int maxGroupSize, SmartDataReader smartDataReader, SmartAccumulationDataStore smartAccumulationDataStore,
                                                  SmartAccumulationsCache smartAccumulationsCache, StoreManager storeManager) {

        this.smartAccumulationsCache = smartAccumulationsCache;
        this.smartDataReader = smartDataReader;
        this.smartAccumulationDataStore = smartAccumulationDataStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.storeManager = storeManager;
    }

    public void run(String configurationName, Instant startDate, Instant endDate, Double accumulationStrategy) throws Exception {
        //strategy for accumulations
        FixedDurationStrategy accumulationDuration = FixedDurationStrategy.fromSeconds(accumulationStrategy.longValue());
        AccumulateSmartRecordsService accumulateSmartRecordsService = new AccumulateSmartRecordsService(accumulationDuration, smartDataReader, pageSize, maxGroupSize, smartAccumulationsCache, smartAccumulationDataStore);
        TimeRange timeRange = new TimeRange(startDate, endDate);
        StoreManagerMetadataProperties storeManagerMetadataProperties = createStoreManagerAwareMetadata(configurationName);
        accumulateSmartRecordsService.execute(timeRange, configurationName, storeManagerMetadataProperties);

        storeManager.cleanupCollections(storeManagerMetadataProperties.getProperties(), startDate);
    }

    public void clean(String configurationName, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }

    public void cleanup(String configurationName, Instant startDate, Instant endDate, Double accumulationStrategy) throws Exception {
        StoreManagerMetadataProperties storeManagerMetadataProperties = createStoreManagerAwareMetadata(configurationName);
        storeManager.cleanupCollections(storeManagerMetadataProperties.getProperties(), startDate, endDate);
    }

    private StoreManagerMetadataProperties createStoreManagerAwareMetadata(String configurationName){
        StoreManagerMetadataProperties storeManagerMetadataProperties = new StoreManagerMetadataProperties();
        storeManagerMetadataProperties.setProperties(CONFIGURATION_NAME, configurationName);
        return storeManagerMetadataProperties;
    }
}

