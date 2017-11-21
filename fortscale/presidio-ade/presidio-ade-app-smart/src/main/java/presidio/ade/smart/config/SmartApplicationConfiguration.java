package presidio.ade.smart.config;

import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartDataStoreConfig;
import presidio.ade.smart.SmartApplicationCommands;
import presidio.ade.smart.SmartScoringService;
import presidio.ade.smart.SmartService;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({
        // Mongo DB related configurations
        MongoDbBulkOpUtilConfig.class,
        // CLI commands related configurations
        SmartApplicationCommands.class,
        // Smart application related configurations
        SmartApplicationSmartRecordConfig.class,
        SmartApplicationAggregationDataReaderConfig.class,
        SmartApplicationSmartScoringServiceConfig.class,
        SmartDataStoreConfig.class,
        StoreManagerConfig.class,
})
public class SmartApplicationConfiguration {
    @Value("${presidio.ade.aggregation.records.threshold:#{null}}")
    private Double aggregationRecordsThreshold;

    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Autowired
    private AggregatedDataReader aggregatedDataReader;
    @Autowired
    private SmartScoringService smartScoringService;
    @Autowired
    private SmartDataStore smartDataStore;
    @Autowired
    private StoreManager storeManager;


    @Bean
    public SmartService smartService() {
        return new SmartService(
                smartRecordConfService,
                aggregationRecordsThreshold,
                aggregatedDataReader,
                smartScoringService,
                smartDataStore,
                storeManager);
    }
}
