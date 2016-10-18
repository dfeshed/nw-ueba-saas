package fortscale.accumulator.aggregation.store.config;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStoreImpl;
import fortscale.accumulator.aggregation.translator.AccumulatedAggregatedFeatureEventTranslator;
import fortscale.accumulator.aggregation.translator.config.AccumulatedAggregatedFeatureEventTranslatorConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Period;


@Configuration
@Import(AccumulatedAggregatedFeatureEventTranslatorConfig.class)
public class AccumulatedAggregatedFeatureEventStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedAggregatedFeatureEventTranslator accumulatedAggregatedFeatureEventTranslator;
    @Autowired
    private StatsService statsService;

    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.aggr.feature.event.retention.daily}')}")
    private Period acmDailyAggrEventRetentionDuration;
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.aggr.feature.event.retention.hourly}')}")
    private Period acmHourlyAggrEventRetentionDuration;

    @Bean
    public AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore()
    {
        return new AccumulatedAggregatedFeatureEventStoreImpl(mongoTemplate,accumulatedAggregatedFeatureEventTranslator,statsService, acmDailyAggrEventRetentionDuration,acmHourlyAggrEventRetentionDuration);
    }
}
