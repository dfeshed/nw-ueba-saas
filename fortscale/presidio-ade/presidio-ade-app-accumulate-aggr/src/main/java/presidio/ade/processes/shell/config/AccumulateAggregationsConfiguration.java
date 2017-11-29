package presidio.ade.processes.shell.config;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.accumulator.aggregation.AccumulationsCacheConfig;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataStore;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.processes.shell.AccumulateAggregationsExecutionService;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketService;

/**
 * Created by maria_dorohin on 7/26/17.
 */
@Configuration
@Import({
        //        application-specific confs
        AccumulateAggregationsBucketServiceConfig.class,
        //        common application confs
        AccumulationsCacheConfig.class,
        AggregationEventsAccumulationDataStoreConfig.class,
        EnrichedDataStoreConfig.class,
        StoreManagerConfig.class,
        NullStatsServiceConfig.class, // TODO: Remove this
})
public class AccumulateAggregationsConfiguration {

    @Autowired
    private AccumulateAggregationsBucketService accumulateAggregationsBucketService;
    @Autowired
    @Qualifier("bucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private AggregationEventsAccumulationDataStore aggregationEventsAccumulationDataStore;
    @Autowired
    private AccumulationsCache accumulationsCache;
    @Value("${feature.aggregation.pageIterator.pageSize}")
    private int pageSize;
    @Value("${feature.aggregation.pageIterator.maxGroupSize}")
    private int maxGroupSize;
    @Autowired
    private StoreManager storeManager;

    @Bean
    public AccumulateAggregationsExecutionService featureAggregationBucketExecutionService() {
        return new AccumulateAggregationsExecutionService(bucketConfigurationService, enrichedDataStore, aggregationEventsAccumulationDataStore, accumulateAggregationsBucketService, accumulationsCache, storeManager, pageSize, maxGroupSize);
    }
}
