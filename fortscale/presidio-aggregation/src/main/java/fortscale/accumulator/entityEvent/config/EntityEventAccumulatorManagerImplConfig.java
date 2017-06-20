package fortscale.accumulator.entityEvent.config;

import fortscale.accumulator.entityEvent.EntityEventAccumulator;
import fortscale.accumulator.entityEvent.EntityEventAccumulatorManagerImpl;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.config.EntityEventConfServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import({EntityEventAccumulatorConfig.class, EntityEventConfServiceConfig.class})
public class EntityEventAccumulatorManagerImplConfig {
    @Autowired
    private EntityEventAccumulator accumulator;
    @Autowired
    private EntityEventConfService entityEventConfService;

    @Bean
    public EntityEventAccumulatorManagerImpl entityEventAccumulatorManager()
    {
        return new EntityEventAccumulatorManagerImpl(accumulator,entityEventConfService);
    }
}
