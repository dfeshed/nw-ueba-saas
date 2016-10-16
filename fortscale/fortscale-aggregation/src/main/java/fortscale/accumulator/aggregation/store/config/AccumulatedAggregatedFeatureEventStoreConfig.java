package fortscale.accumulator.aggregation.store.config;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStoreImpl;
import fortscale.accumulator.aggregation.translator.AccumulatedAggregatedFeatureEventTranslator;
import fortscale.accumulator.aggregation.translator.config.AccumulatedAggregatedFeatureEventTranslatorConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@Import(AccumulatedAggregatedFeatureEventTranslatorConfig.class)
public class AccumulatedAggregatedFeatureEventStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedAggregatedFeatureEventTranslator translator;
    @Autowired
    private StatsService statsService;

    @Bean
    public AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore()
    {
        return new AccumulatedAggregatedFeatureEventStoreImpl(mongoTemplate,translator,statsService);
    }
}
