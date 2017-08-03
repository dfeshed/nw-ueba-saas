package presidio.ade.processes.shell.config;

import fortscale.accumulator.AccumulationsStore;
import fortscale.accumulator.AccumulationsStoreConfig;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AccumulatedDataStore;
import presidio.ade.domain.store.accumulator.AccumulatedDataStoreConfig;
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
        AccumulationsStoreConfig.class,
        AccumulatedDataStoreConfig.class,
        EnrichedDataStoreConfig.class,
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
    private AccumulatedDataStore accumulatedDataStore;
    @Autowired
    private AccumulationsStore accumulationsStore;
    @Value("${feature.aggregation.pageSize}")
    private int pageSize;
    @Value("${feature.aggregation.maxGroupSize}")
    private int maxGroupSize;

    @Bean
    public AccumulateAggregationsExecutionService featureAggregationBucketExecutionService() {
        return new AccumulateAggregationsExecutionService(bucketConfigurationService, enrichedDataStore, accumulatedDataStore, accumulateAggregationsBucketService, accumulationsStore, pageSize, maxGroupSize);
    }
}
