package fortscale.accumulator.entityEvent.store.config;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStoreImpl;
import fortscale.accumulator.entityEvent.translator.AccumulatedEntityEventTranslator;
import fortscale.accumulator.entityEvent.translator.config.AccumulatedEntityEventTranslatorConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Period;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import(AccumulatedEntityEventTranslatorConfig.class)
public class AccumulatedEntityEventStoreConfig {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedEntityEventTranslator accumulatedEntityEventTranslator;
    @Autowired
    private StatsService statsService;
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.entity.event.retention.daily}')}")
    private Period acmDailyEntityEventRetentionDuration;
    @Value("#{ T(java.time.Period).parse('${fortscale.accumulator.entity.event.retention.hourly}')}")
    private Period acmHourlyEntityEventRetentionDuration;

    @Bean
    public AccumulatedEntityEventStore accumulatedEntityEventStore()
    {
        return new AccumulatedEntityEventStoreImpl(mongoTemplate,accumulatedEntityEventTranslator,statsService, acmDailyEntityEventRetentionDuration, acmHourlyEntityEventRetentionDuration);
    }
}
