package fortscale.accumulator.entityEvent.store.config;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStoreImpl;
import fortscale.accumulator.entityEvent.translator.AccumulatedEntityEventTranslator;
import fortscale.accumulator.entityEvent.translator.config.AccumulatedEntityEventTranslatorConfig;
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
@Import(AccumulatedEntityEventTranslatorConfig.class)
public class AccumulatedEntityEventStoreConfig {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedEntityEventTranslator translator;
    @Autowired
    private StatsService statsService;

    @Bean
    public AccumulatedEntityEventStore accumulatedEntityEventStore()
    {
        return new AccumulatedEntityEventStoreImpl(mongoTemplate,translator,statsService);
    }
}
