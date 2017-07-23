package fortscale.aggregation.feature.event.store;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.aggregation.feature.event.store.config.AggregatedFeatureEventsMongoStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/23/17.
 */
@Configuration
@Import({AggregatedFeatureEventsMongoStoreConfig.class,AccumulatedAggregatedFeatureEventStoreConfig.class})
public class AggregatedFeatureEventsReaderServiceConfig {
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;

    @Bean
    public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
        return new AggregatedFeatureEventsReaderService(aggregatedFeatureEventsMongoStore,accumulatedAggregatedFeatureEventStore);
    }
}
