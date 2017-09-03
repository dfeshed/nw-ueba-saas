package presidio.ade.processes.shell.config;


import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.accumulator.smart.SmartAccumulationsCacheConfig;
import fortscale.utils.ttl.TtlService;
import fortscale.utils.ttl.TtlServiceConfig;
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
        TtlServiceConfig.class,
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
    private TtlService ttlService;


    @Bean
    public AccumulateSmartRecordsExecutionService accumulateSmartRecordsExecutionService() {
        return new AccumulateSmartRecordsExecutionService(pageSize, maxGroupSize, smartDataReader, smartAccumulationDataStore, smartAccumulationsCache, ttlService);
    }
}
