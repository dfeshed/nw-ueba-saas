package fortscale.accumulator.aggregation.config;

import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulator;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.config.AggregatedFeatureEventsMongoStoreConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Period;

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
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.aggr.feature.event.from.period.ago.daily}')}")
    private Period defaultAggrEventFromPeriodDaily;
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.aggr.feature.event.from.period.ago.hourly}')}")
    private Period defaultAggrEventFromPeriodHourly;

    @Bean
    public AggregatedFeatureEventsAccumulator aggregatedFeatureEventsAccumulator() {
        return new AggregatedFeatureEventsAccumulator(aggregatedFeatureEventsMongoStore,
                accumulatedAggregatedFeatureEventStore, statsService,
                defaultAggrEventFromPeriodDaily, defaultAggrEventFromPeriodHourly);
    }

}
