package presidio.ade.sdk.common;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartDataStoreMongoConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventConfServiceConfig;

/**
 * @author Barak Schuster
 */
@Configuration
@Import({EnrichedDataStoreConfig.class,
        SmartDataStoreMongoConfig.class,
        ScoredEnrichedDataStoreMongoConfig.class,
        AggregatedFeatureEventConfServiceConfig.class})
public class AdeManagerSdkConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Autowired
    private SmartDataStore smartDataStore;

    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;

    @Autowired
    @Qualifier("adeManagerSdkAggregatedFeatureEventsConfService")
    private AggregatedFeatureEventsConfService adeManagerSdkAggregatedFeatureEventsConfService;

    @Bean
    public AdeManagerSdk adeManagerSdk() {
        return new AdeManagerSdkImpl(enrichedDataStore, smartDataStore, scoredEnrichedDataStore, adeManagerSdkAggregatedFeatureEventsConfService);
    }
}
