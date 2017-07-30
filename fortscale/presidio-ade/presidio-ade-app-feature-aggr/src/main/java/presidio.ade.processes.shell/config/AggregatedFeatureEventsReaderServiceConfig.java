package presidio.ade.processes.shell.config;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import(        {
        AccumulatedAggregatedFeatureEventStoreConfig.class,
        AggregatedFeatureEventsMongoStoreConfig.class
})
public class AggregatedFeatureEventsReaderServiceConfig {
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;

    @Bean
    public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
        return new AggregatedFeatureEventsReaderService(aggregatedFeatureEventsMongoStore, accumulatedAggregatedFeatureEventStore);
    }
}
