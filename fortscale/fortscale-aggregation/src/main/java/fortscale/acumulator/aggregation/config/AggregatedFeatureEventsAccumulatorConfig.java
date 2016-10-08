package fortscale.acumulator.aggregation.config;

import fortscale.acumulator.aggregation.AggregatedFeatureEventsAccumulator;
import fortscale.acumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.acumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.config.AggregatedFeatureEventsMongoStoreConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
@Import({AccumulatedAggregatedFeatureEventStoreConfig.class,
        AggregatedFeatureEventsMongoStoreConfig.class})
public class AggregatedFeatureEventsAccumulatorConfig {
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;
    @Autowired
    private StatsService statsService;

    @Bean
    public AggregatedFeatureEventsAccumulator aggregatedFeatureEventsAccumulator() {
        return new AggregatedFeatureEventsAccumulator(aggregatedFeatureEventsMongoStore, accumulatedAggregatedFeatureEventStore, statsService);
    }

}
