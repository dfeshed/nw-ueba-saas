package presidio.ade.processes.shell.config;


import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.accumulator.smart.SmartAccumulationsCacheConfig;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStoreConfig;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartDataReaderConfig;
import presidio.ade.processes.shell.AccumulateSmartRecordsExecutionService;

@Configuration
@Import({
        SmartDataReaderConfig.class,
        SmartAccumulationsCacheConfig.class,
        SmartAccumulationDataStoreConfig.class,
        StoreManagerConfig.class,
})
public class AccumulateSmartConfiguration {

    @Autowired
    private SmartDataReader smartDataReader;
    @Autowired
    private SmartAccumulationsCache smartAccumulationsCache;
    @Autowired
    private SmartAccumulationDataStore smartAccumulationDataStore;
    @Value("${smart.pageIterator.pageSize}")
    private int pageSize;
    @Value("${smart.pageIterator.maxGroupSize}")
    private int maxGroupSize;
    @Autowired
    private StoreManager storeManager;


    @Bean
    public AccumulateSmartRecordsExecutionService accumulateSmartRecordsExecutionService() {
        return new AccumulateSmartRecordsExecutionService(pageSize, maxGroupSize, smartDataReader, smartAccumulationDataStore, smartAccumulationsCache, storeManager);
    }
}
