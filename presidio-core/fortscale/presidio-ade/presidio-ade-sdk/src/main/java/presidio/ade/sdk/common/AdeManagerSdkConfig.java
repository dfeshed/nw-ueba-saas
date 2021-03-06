package presidio.ade.sdk.common;

import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.store.StoreManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.enriched.StoreManagerAwareEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartDataReaderConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventsConfServiceConfig;
import presidio.ade.sdk.aggregation_records.BucketConfigurationServiceConfig;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordSplitter;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordSplitterConfig;
import presidio.ade.sdk.smart_records.SmartRecordConfServiceConfig;
import presidio.ade.sdk.store.StoreManagerConfig;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * @author Barak Schuster
 */
@Configuration
@Import({
        EnrichedDataStoreConfig.class,
        SmartDataReaderConfig.class,
        ScoredEnrichedDataStoreMongoConfig.class,
        AggregatedDataStoreConfig.class,
        BucketConfigurationServiceConfig.class,
        AggregatedFeatureEventsConfServiceConfig.class,
        FeatureBucketStoreMongoConfig.class,
        AggregationEventsAccumulationDataReaderConfig.class,
        SmartRecordConfServiceConfig.class,
        StoreManagerConfig.class,
        PresidioMonitoringConfiguration.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        ScoreAggregationRecordSplitterConfig.class
})
public class AdeManagerSdkConfig {
    @Autowired
    private SmartDataReader smartDataReader;

    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;

    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Autowired
    private FeatureBucketReader featureBucketReader;

    @Autowired
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    @Autowired
    private SmartRecordConfService smartRecordConfService;

    @Autowired
    private StoreManager storeManager;

    @Autowired
    private StoreManagerAwareEnrichedDataStore storeManagerAwareEnrichedDataStore;

    @Autowired
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;

    @Autowired
    private ScoreAggregationRecordSplitter scoreAggregationRecordSplitter;

    @Bean
    public AdeManagerSdk adeManagerSdk() {
        return new AdeManagerSdkImpl(
                storeManagerAwareEnrichedDataStore,
                smartDataReader,
                scoredEnrichedDataStore,
                bucketConfigurationService,
                aggregatedFeatureEventsConfService,
                featureBucketReader,
                aggregationEventsAccumulationDataReader,
                smartRecordConfService,
                storeManager,
                inMemoryFeatureBucketAggregator,
                scoreAggregationRecordSplitter);
    }
}
