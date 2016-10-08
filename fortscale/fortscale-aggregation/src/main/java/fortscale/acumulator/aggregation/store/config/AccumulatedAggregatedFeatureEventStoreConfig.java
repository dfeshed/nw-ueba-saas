package fortscale.acumulator.aggregation.store.config;

import fortscale.acumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.acumulator.aggregation.store.AccumulatedAggregatedFeatureEventStoreImpl;
import fortscale.acumulator.translator.AccumulatedFeatureTranslator;
import fortscale.acumulator.translator.config.AccumulatedFeatureTranslatorConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@Import(AccumulatedFeatureTranslatorConfig.class)
public class AccumulatedAggregatedFeatureEventStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedFeatureTranslator translator;
    @Autowired
    private StatsService statsService;

    @Bean
    public AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore()
    {
        return new AccumulatedAggregatedFeatureEventStoreImpl(mongoTemplate,translator,statsService);
    }
}
