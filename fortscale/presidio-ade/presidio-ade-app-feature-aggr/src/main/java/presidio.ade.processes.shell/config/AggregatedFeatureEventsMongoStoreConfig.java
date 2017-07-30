package presidio.ade.processes.shell.config;

import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Import({
        AggregatedFeatureNameTranslationServiceConfig.class,
        FeatureAggregationBucketConfigurationServiceConfig.class
})
@Configuration

public class AggregatedFeatureEventsMongoStoreConfig {
    @Bean
    public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore()
    {
        return new AggregatedFeatureEventsMongoStore();
    }
}
