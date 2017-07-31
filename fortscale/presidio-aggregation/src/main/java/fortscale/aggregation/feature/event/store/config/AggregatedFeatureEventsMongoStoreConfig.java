package fortscale.aggregation.feature.event.store.config;

import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        AggregatedFeatureNameTranslationServiceConfig.class})
public class AggregatedFeatureEventsMongoStoreConfig {
    @Bean
    public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore()
    {
        return new AggregatedFeatureEventsMongoStore();
    }
}
