package fortscale.accumulator.entityEvent.store.config;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStoreImpl;
import fortscale.accumulator.translator.AccumulatedFeatureTranslator;
import fortscale.accumulator.translator.config.AccumulatedFeatureTranslatorConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import(AccumulatedFeatureTranslatorConfig.class)
public class AccumulatedEntityEventStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedFeatureTranslator translator;
    @Autowired
    private StatsService statsService;

    @Bean
    public AccumulatedEntityEventStore accumulatedEntityEventStore()
    {
        return new AccumulatedEntityEventStoreImpl(mongoTemplate,translator,statsService);
    }
}
