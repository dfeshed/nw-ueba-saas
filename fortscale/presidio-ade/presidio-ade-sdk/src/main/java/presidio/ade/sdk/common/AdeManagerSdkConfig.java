package presidio.ade.sdk.common;

import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartDataReaderConfig;
import presidio.ade.domain.store.smart.SmartDataStoreConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventConfServiceConfig;

/**
 * @author Barak Schuster
 */
@Configuration
@Import({EnrichedDataStoreConfig.class,
        SmartDataReaderConfig.class,
        ScoredEnrichedDataStoreMongoConfig.class,
        AggregatedFeatureEventConfServiceConfig.class,
        FeatureBucketStoreMongoConfig.class,
        AggregationEventsAccumulationDataReaderConfig.class
})
public class AdeManagerSdkConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Autowired
    private SmartDataReader smartDataReader;

    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;

    @Autowired
    private FeatureBucketReader featureBucketReader;

    @Autowired
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    @Autowired
    @Qualifier("adeManagerSdkAggregatedFeatureEventsConfService")
    private AggregatedFeatureEventsConfService adeManagerSdkAggregatedFeatureEventsConfService;

    @Bean
    public AdeManagerSdk adeManagerSdk() {
        return new AdeManagerSdkImpl(enrichedDataStore, smartDataReader, scoredEnrichedDataStore, adeManagerSdkAggregatedFeatureEventsConfService, featureBucketReader, aggregationEventsAccumulationDataReader);
    }
}
