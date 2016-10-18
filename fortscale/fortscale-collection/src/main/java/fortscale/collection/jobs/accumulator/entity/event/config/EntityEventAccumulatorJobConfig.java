package fortscale.collection.jobs.accumulator.entity.event.config;

import fortscale.accumulator.entityEvent.config.EntityEventAccumulatorManagerImplConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/18/16.
 */
@Configuration
@Import({EntityEventAccumulatorManagerImplConfig.class})
public class EntityEventAccumulatorJobConfig {
}
