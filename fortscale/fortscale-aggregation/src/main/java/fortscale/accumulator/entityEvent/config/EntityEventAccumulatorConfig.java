package fortscale.accumulator.entityEvent.config;

import fortscale.accumulator.entityEvent.EntityEventAccumulator;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.config.AccumulatedEntityEventStoreConfig;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.entity.event.config.EntityEventMongoStoreConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Period;

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
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.entity.event.from.period.ago.daily}')}")
    private Period defaultEntityEventFromPeriodDaily;
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.entity.event.from.period.ago.hourly}')}")
    private Period defaultEntityEventFromPeriodHourly;

    @Bean
    public EntityEventAccumulator entityEventAccumulator()
    {
        return new EntityEventAccumulator(entityEventMongoStore,accumulatedEntityEventStore,statsService,
                defaultEntityEventFromPeriodDaily, defaultEntityEventFromPeriodHourly);
    }
}
