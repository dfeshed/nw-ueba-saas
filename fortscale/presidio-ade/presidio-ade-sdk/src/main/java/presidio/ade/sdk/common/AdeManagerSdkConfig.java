package presidio.ade.sdk.common;

import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.ttl.TtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStoreImplMongo;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartDataReaderConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventsConfServiceConfig;
import presidio.ade.sdk.smart_records.SmartRecordConfServiceConfig;
import presidio.ade.sdk.ttl.TtlServiceConfig;

/**
 * @author Barak Schuster
 */
@Configuration
@Import({
        EnrichedDataStoreConfig.class,
        SmartDataReaderConfig.class,
        ScoredEnrichedDataStoreMongoConfig.class,
        AggregatedFeatureEventsConfServiceConfig.class,
        FeatureBucketStoreMongoConfig.class,
        AggregationEventsAccumulationDataReaderConfig.class,
        SmartRecordConfServiceConfig.class,
        TtlServiceConfig.class
})
public class AdeManagerSdkConfig {
    @Autowired
    private SmartDataReader smartDataReader;

    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Autowired
    private FeatureBucketReader featureBucketReader;

    @Autowired
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    @Autowired
    private SmartRecordConfService smartRecordConfService;

    @Autowired
    private TtlService ttlService;

    @Autowired
    private EnrichedDataStoreImplMongo enrichedDataStoreImplMongo;

    @Bean
    public AdeManagerSdk adeManagerSdk() {
        return new AdeManagerSdkImpl(
                enrichedDataStoreImplMongo,
                smartDataReader,
                scoredEnrichedDataStore,
                aggregatedFeatureEventsConfService,
                featureBucketReader,
                aggregationEventsAccumulationDataReader,
                smartRecordConfService,
                ttlService);
    }
}
