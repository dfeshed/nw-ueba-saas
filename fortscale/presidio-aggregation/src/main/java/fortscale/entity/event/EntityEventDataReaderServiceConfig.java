package fortscale.entity.event;

import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.accumulator.entityEvent.store.config.AccumulatedEntityEventStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/23/17.
 */
@Configuration
@Import({EntityEventDataMongoStoreConfig.class,AccumulatedEntityEventStoreConfig.class})
public class EntityEventDataReaderServiceConfig {
    @Autowired
    private EntityEventDataMongoStore entityEventDataMongoStore;
    @Autowired
    private AccumulatedEntityEventStore accumulatedEntityEventStore;

    @Bean
    public EntityEventDataReaderService entityEventDataReaderService() {
        return new EntityEventDataReaderService(entityEventDataMongoStore,accumulatedEntityEventStore);
    }
}
