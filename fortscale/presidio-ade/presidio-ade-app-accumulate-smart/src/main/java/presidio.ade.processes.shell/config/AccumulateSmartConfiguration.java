package presidio.ade.processes.shell.config;


import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.accumulator.smart.SmartAccumulationsCacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStore;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStoreConfig;
import presidio.ade.domain.store.aggr.smart.SmartRecordDataReader;
import presidio.ade.domain.store.aggr.smart.SmartRecordsDataReaderConfig;
import presidio.ade.processes.shell.AccumulateSmartRecordsExecutionService;

@Configuration
@Import({
        SmartRecordsDataReaderConfig.class,
        SmartAccumulationsCacheConfig.class,
        SmartAccumulationDataStoreConfig.class,
})
public class AccumulateSmartConfiguration {

    @Autowired
    private SmartRecordDataReader smartRecordDataReader;
    @Autowired
    private SmartAccumulationsCache smartAccumulationsCache;
    @Autowired
    private SmartAccumulationDataStore smartAccumulationDataStore;
    @Value("${smart.pageIterator.pageSize}")
    private int pageSize;
    @Value("${smart.pageIterator.maxGroupSize}")
    private int maxGroupSize;


    @Bean
    public AccumulateSmartRecordsExecutionService accumulateSmartRecordsExecutionService() {
        return new AccumulateSmartRecordsExecutionService(pageSize, maxGroupSize, smartRecordDataReader, smartAccumulationDataStore, smartAccumulationsCache);
    }
}
