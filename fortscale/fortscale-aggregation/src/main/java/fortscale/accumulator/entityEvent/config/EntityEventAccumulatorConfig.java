package fortscale.accumulator.entityEvent.config;

import fortscale.accumulator.entityEvent.EntityEventAccumulator;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.config.AccumulatedEntityEventStoreConfig;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.entity.event.config.EntityEventMongoStoreConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import({AccumulatedEntityEventStoreConfig.class,EntityEventMongoStoreConfig.class})
public class EntityEventAccumulatorConfig {

    @Autowired
    private EntityEventMongoStore entityEventMongoStore;
    @Autowired
    private AccumulatedEntityEventStore accumulatedEntityEventStore;
    @Autowired
    private StatsService statsService;

    @Bean
    public EntityEventAccumulator entityEventAccumulator()
    {
        return new EntityEventAccumulator(entityEventMongoStore,accumulatedEntityEventStore,statsService);
    }
}
